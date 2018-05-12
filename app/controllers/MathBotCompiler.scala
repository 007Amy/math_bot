package controllers

import java.net.URLDecoder
import javax.inject.Inject

import actors.LevelGenerationActor.{ActorFailed, GetGridMap}
import actors.StatsActor.{StatsDoneUpdating, UpdateStats}
import actors.{LevelGenerationActor, StatsActor}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import compiler.processor.{AnimationType, Frame, Processor}
import compiler.{Cell, Compiler, GridAndProgram, Point}
import controllers.MathBotCompiler.ClientFrame
import loggers.MathBotLogger
import model.PlayerTokenModel
import model.models._
import play.api.Environment
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.Future
import scala.concurrent.duration._

case class LevelAndStep(level: String, step: String)

object MathBotCompiler {

  case class ClientCell(location: Point, items: List[String])

  object ClientCell {
    def apply(loc: Point, cell: Cell): ClientCell = {
      val contents = cell.contents.map(element => element.image)
      ClientCell(loc, contents)
    }
  }

  case class ClientGrid(cells: Set[ClientCell])

  object ClientGrid {
    def apply(grid: compiler.Grid): ClientGrid = {
      val cells =
        grid.grid.map(g => ClientCell(Point(g._1._1, g._1._2), g._2)).toSet
      ClientGrid(cells)
    }
  }

  case class ClientRobotState(location: Point,
                              orientation: String,
                              holding: List[String],
                              animation: Option[AnimationType.Value],
                              grid: Option[ClientGrid])

  object ClientRobotState {
    def apply(frame: Frame): ClientRobotState = new ClientRobotState(
      location = frame.robotLocation.map(l => Point(l.x, l.y)).getOrElse(Point(0, 0)),
      orientation = frame.robotLocation.map(l => l.orientation).getOrElse("0"),
      animation = frame.register.animation,
      grid = Some(ClientGrid(frame.board)),
      holding = frame.register.holdingCell.contents.map(v => v.image)
    )

    def toTuple(state: ClientRobotState) =
      Some((state.location.x, state.location.y, state.orientation, state.animation.map(a => a.toString)))

  }

  case class ClientFrame(robotState: ClientRobotState, programState: String)

  object ClientFrame {
    def apply(frame: Frame): ClientFrame = ClientFrame(frame, "running")

    def success(frame: Frame): ClientFrame = ClientFrame(frame, "success")

    def failure(frame: Frame): ClientFrame = ClientFrame(frame, "failure")

    def apply(frame: Frame, programState: String): ClientFrame =
      ClientFrame(ClientRobotState(frame), programState)
  }

  case class ClientResponse(clientFrames: List[ClientFrame], problem: Problem)

  def jsonify(clientFrames: List[ClientFrame], problem: Problem = Problem("0")): JsValue = {

    implicit val pointWrites: Writes[Point] = (
      (JsPath \ "x").write[Int] and
      (JsPath \ "y").write[Int]
    )(unlift(Point.unapply))

    implicit val clientCellWrites: Writes[ClientCell] = (
      (JsPath \ "location").write[Point] and
      (JsPath \ "items").write[List[String]]
    )(unlift(ClientCell.unapply))

    implicit val gridWrites = new Writes[ClientGrid] {
      override def writes(o: ClientGrid): JsValue = Json.obj(
        "cells" -> Json.toJson(o.cells.toList)
      )
    }

    implicit val robotStateWrite: Writes[ClientRobotState] = (
      (JsPath \ "location").write[Point] and
      (JsPath \ "orientation").write[String] and
      (JsPath \ "holding").write[List[String]] and
      (JsPath \ "animation").write[Option[AnimationType.Value]] and
      (JsPath \ "grid").write[Option[ClientGrid]]
    )(unlift(ClientRobotState.unapply))

    implicit val clientFrameWrites: Writes[ClientFrame] = (
      (JsPath \ "robotState").write[ClientRobotState] and
      (JsPath \ "programState").write[String]
    )(unlift(ClientFrame.unapply))

    implicit val problemWrites = new Writes[Problem] {
      def writes(o: Problem) = JsString(o.problem)
    }

    implicit val clientResponse: Writes[ClientResponse] = (
      (JsPath \ "frames").write[List[ClientFrame]] and
      (JsPath \ "programState").write[Problem]
    )(unlift(ClientResponse.unapply))

    Json.toJson(ClientResponse(clientFrames, problem))
  }
}

class MathBotCompiler @Inject()(val reactiveMongoApi: ReactiveMongoApi)(implicit system: ActorSystem,
                                                                        mat: Materializer,
                                                                        mathBotLogger: MathBotLogger,
                                                                        environment: Environment)
    extends Controller
    with PlayerTokenModel
    with utils.SameOriginCheck {

  val levelActor =
    system.actorOf(LevelGenerationActor.props(reactiveMongoApi, mathBotLogger, environment), "level-compiler-actor")
  val statsActor = system.actorOf(StatsActor.props(system, reactiveMongoApi, mathBotLogger), "stats-compiler-actor")

  def wsPath(tokenId: String): Action[AnyContent] = Action { implicit request: RequestHeader =>
    val url = routes.MathBotCompiler.compileWs(tokenId).webSocketURL()
    val changeSsl =
      if (url.contains("localhost")) url else url.replaceFirst("ws", "wss")
    Ok(changeSsl)
  }

  def compileWs(encodedTokenId: String): WebSocket =
    WebSocket.accept[JsValue, JsValue] {
      case rh if sameOriginCheck(rh) =>
        ActorFlow.actorRef(
          out =>
            CompilerActor.props(out,
                                URLDecoder.decode(encodedTokenId, "UTF-8"),
                                reactiveMongoApi,
                                statsActor,
                                levelActor,
                                mathBotLogger)
        )
      case rejected =>
        ActorFlow.actorRef(out => {
          SameOriginFailedActor.props(out, mathBotLogger)
        })
    }

  def compile(encodedTokenId: String) = Action.async { implicit request =>
    class FakeActor extends Actor {

      override def receive: Receive = {
        case _ =>
      }
    }

    val fakeActorProps = Props(new FakeActor)
    val fakeActor = system.actorOf(fakeActorProps)

    implicit val timeout: Timeout = 500.seconds

    val sr = SocketRequest(Some(600), request.body.asJson, Some(false))

    val compilerProps =
      CompilerActor.props(fakeActor,
                          URLDecoder.decode(encodedTokenId, "UTF-8"),
                          reactiveMongoApi,
                          statsActor,
                          levelActor,
                          mathBotLogger)
    val compiler = system.actorOf(compilerProps)

    (compiler ? sr)
      .mapTo[SocketResponse]
      .map(sr => {
        Ok(sr.response)
      })

  }
}

object StringUtils {

  implicit class StringImprovements(val s: String) {

    import scala.util.control.Exception._

    def toIntOpt = catching(classOf[NumberFormatException]) opt s.toInt
  }

}

case class ProgramState(stream: Stream[Frame],
                        grid: GridMap,
                        program: GridAndProgram,
                        clientFrames: List[ClientFrame] = List.empty[ClientFrame],
                        previousSteps: Int = 0)

case class SocketRequest(steps: Option[Int], program: Option[JsValue], halt: Option[Boolean])

case class SocketResponse(response: JsValue)

case class SomethingBroke(msg: JsValue)

class CompilerActor @Inject()(out: ActorRef, tokenId: String)(
    val reactiveMongoApi: ReactiveMongoApi,
    statsActor: ActorRef,
    levelActor: ActorRef,
    logger: MathBotLogger
) extends Actor
    with PlayerTokenModel {

  import MathBotCompiler._
  import akka.pattern.pipe

  implicit val timeout: Timeout = 5000.minutes

  implicit val sockRequestReads: Reads[SocketRequest] = (
    (JsPath \ "steps").readNullable[Int] and
    (JsPath \ "program").readNullable[JsValue] and
    (JsPath \ "halt").readNullable[Boolean]
  )(SocketRequest.apply _)

  var current: Future[Option[ProgramState]] = Future(None)

  private val className = "CompilerActor"

  override def receive: Receive = {
    case msg: JsValue =>
      val possibleSocketRequest = Json.fromJson[SocketRequest](msg)

      possibleSocketRequest.asOpt foreach { request =>
        self ! request
      }

    case socketResponse: SocketResponse =>
      logger.LogResponse(className, "Compiler responded.")
      out ! socketResponse.response

    case socketRequest: SocketRequest =>
      val killProgram =
        socketRequest.halt.map(v => Future(Option.empty[ProgramState]))
      logger.LogInfo(className, "Compiler starting.")
      val newprogram =
        socketRequest.program
          .map { json =>
            for {
              tokenList <- getToken(tokenId)
              grid <- (levelActor ? GetGridMap(tokenList)).mapTo[GridMap]
            } yield
              for {
                token <- tokenList.headOption
                main = token.lambdas.head.main
                funcs = token.lambdas.head.activeFuncs
                commands = token.lambdas.head.cmds
                program <- Compiler.compile(json, main, funcs, commands, grid, grid.problem)
              } yield {
                val processor = Processor(program)
                val stream = processor.execute()
                ProgramState(stream = stream, program = program, grid = grid, previousSteps = 0)
              }
          }

      val moreProgram = socketRequest.steps.map { steps =>
        for {
          possibleProgramState <- newprogram.getOrElse(killProgram.getOrElse(current))
        } yield
          for {
            programState <- possibleProgramState
          } yield {
            val executeSomeFrames = programState.stream
              .slice(programState.previousSteps, programState.previousSteps + steps)
              .toList
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
              .scanLeft((Option(ClientGrid(cells = Set.empty[ClientCell])), Set.empty[ClientCell])) {
                (previous, current) =>
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

            programState.copy(
              clientFrames = lastFrame.map(f => deduped :+ f).getOrElse(deduped),
              previousSteps = programState.previousSteps + steps
            )
          }
      }

      current = moreProgram.getOrElse(for {
        nextCurrent <- current
      } yield nextCurrent.map(p => p.copy(clientFrames = List.empty[ClientFrame])))

      val f = for {
        possibleNextCurrent <- current
      } yield
        possibleNextCurrent match {
          case Some(nextCurrent) =>
            for {
              _ <- statsActor ? UpdateStats(nextCurrent.clientFrames.last.programState == "success", tokenId)
            } yield SocketResponse(jsonify(nextCurrent.clientFrames, nextCurrent.program.problem))
          case None => Future { SocketResponse(JsString("Compiler failed")) }
        }

      f.flatMap(v => v) pipeTo sender()

    case Left(statsDoneUpdating: StatsDoneUpdating) =>
      logger.LogResponse(className, s"Stats updated successfully. token_id:$tokenId")

    case Right(invalidJson: ActorFailed) =>
      logger.LogFailure(className, invalidJson.msg)
      self ! invalidJson.msg

    case _ => out ! JsString("Caught it!")
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

class SameOriginFailedActor(out: ActorRef, logger: MathBotLogger) extends Actor {

  private val className = "SameOriginFailedActor"

  val msg = "Failed same origin check."

  override def receive: Receive = {
    case _ =>
      out ! JsString(msg)
  }
}

object SameOriginFailedActor {
  def props(out: ActorRef, logger: MathBotLogger) = Props(new SameOriginFailedActor(out, logger))
}
