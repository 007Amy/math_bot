package actors

import actors.LevelGenerationActor.GetGridMap
import actors.StatsActor.{ StatsDoneUpdating, UpdateStats }
import actors.messages._
import akka.actor.{ Actor, ActorRef, Props }
import akka.pattern.ask
import akka.util.Timeout
import compiler.operations.NoOperation
import compiler.processor.{ Frame, Processor }
import compiler.{ Compiler, GridAndProgram }
import controllers.MathBotCompiler
import javax.inject.Inject
import loggers.MathBotLogger
import model.PlayerTokenModel
import model.models.GridMap
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.duration._

class CompilerActor @Inject()(out: ActorRef, tokenId: String)(
    val reactiveMongoApi: ReactiveMongoApi,
    statsActor: ActorRef,
    levelActor: ActorRef,
    logger: MathBotLogger
) extends Actor
    with PlayerTokenModel {

  import MathBotCompiler._

  case class ProgramState(stream: Stream[Frame],
                          iterator: Iterator[Frame],
                          grid: GridMap,
                          program: GridAndProgram,
                          clientFrames: List[ClientFrame] = List.empty[ClientFrame],
                          previousSteps: Int = 0)

  implicit val timeout: Timeout = 5000.minutes

  var currentCompiler: Option[ProgramState] = None

  private val className = "CompilerActor"

  override def receive: Receive = {
    case CompilerExecute(steps, problem) =>
      currentCompiler match {
        case Some(_) => self ! CompilerStep(steps, problem)
        case None => self ! CompilerCreate(steps, problem)

      }

    case CompilerCreate(steps, problem) =>
      logger.LogInfo(className, "Creating new compiler.")

      for {
        tokenList <- getToken(tokenId)
        grid <- (levelActor ? GetGridMap(tokenList)).mapTo[GridMap]
      } yield {
        for {
          token <- tokenList
          main = token.lambdas.head.main
          funcs = token.lambdas.head.activeFuncs
          commands = token.lambdas.head.cmds
          program <- Compiler.compile( /*json,*/ main, funcs, commands, grid, problem)
        } yield {
          val processor = Processor(program)
          val stream = processor.execute()
          currentCompiler = Some(
            ProgramState(stream = stream, iterator = stream.iterator, program = program, grid = grid)
          )
          self ! CompilerStep(steps, problem)
        }
      }

    case CompilerStep(steps, problem) =>
      logger.LogInfo(className, s"Stepping compiler for $steps")
      for {
        programState <- currentCompiler
      } yield {
        val executeSomeFrames = programState.iterator
          .slice(programState.previousSteps, programState.previousSteps + steps)
          .toList
        if (executeSomeFrames.nonEmpty) {
          val success = programState.grid
            .success(executeSomeFrames.last, programState.program.problem)
          val clientFrames = executeSomeFrames
            .filter(f => f.robotLocation.isDefined)
            .map(f => ClientFrame(f))
          val lastFrame =
            if (success) {
              Some(MathBotCompiler.ClientFrame.success(executeSomeFrames.last))
            } else None
          val taggedDuplicates = clientFrames
            .map(c => (c.robotState.grid, Set.empty[ClientCell]))
            .scanLeft((Option(ClientGrid(cells = Set.empty[ClientCell])), Set.empty[ClientCell])) { (previous, current) =>
              val changes = for {
                p <- previous._1
                c <- current._1
              } yield {
                val prevDiff = p.cells diff c.cells
                val currDiff = c.cells diff p.cells
                val modifiedpart1 = (prevDiff ++ currDiff)
                  .groupBy(c => c.location)
                  .filter(g => g._2.size > 1)
                val modifidepart2 =
                  c.cells.filter(p => modifiedpart1.contains(p.location))
                val removed = (prevDiff diff currDiff).filter(p => !modifiedpart1.contains(p.location))
                val newcells = (currDiff diff prevDiff).filter(p => !modifiedpart1.contains(p.location))
                removed.map(c => c.copy(items = List.empty[String])) ++ modifidepart2 ++ newcells
              }
              (current._1, changes.getOrElse(Set.empty[ClientCell]))
            }
          val deduped = taggedDuplicates.tail
            .zip(clientFrames)
            .map(
              v =>
                v._2.copy(
                  robotState = v._2.robotState
                    .copy(grid = Some(ClientGrid(cells = v._1._2)))
                )
            )

          if (lastFrame.exists(_.programState == "success"))
            statsActor ! UpdateStats(success = true, tokenId)

          out ! CompilerOutput(lastFrame.map(f => deduped :+ f).getOrElse(deduped), problem)
        } else {
          val nopFrame = currentCompiler.get.stream.last.copy(operation = NoOperation())
          currentCompiler = None
          out ! CompilerOutput(List(ClientFrame.failure(nopFrame)), problem)
        }
      }

    case _ : CompilerHalt =>
      logger.LogInfo(className, "Compiler halted")
      currentCompiler = None
      out ! CompilerHalted()

    case Left(_ : StatsDoneUpdating) =>
      logger.LogResponse(className, s"Stats updated successfully. token_id:$tokenId")

    case Right(invalidJson: ActorFailed) =>
      logger.LogFailure(className, invalidJson.msg)
      self ! invalidJson.msg

    case _ => out ! ActorFailed("Unknown command submitted to compiler")
  }
}

object CompilerActor {
  def props(out: ActorRef,
            tokenId: String,
            reactiveMongoApi: ReactiveMongoApi,
            statsActor: ActorRef,
            levelActor: ActorRef,
            logger: MathBotLogger) =
    Props(new CompilerActor(out, tokenId)(reactiveMongoApi, statsActor, levelActor, logger))
}
