package controllers

import java.net.URLDecoder

import actors._
import akka.actor.{ Actor, ActorSystem, Props }
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import compiler.processor.{ AnimationType, Frame }
import compiler.{ Cell, Point }
import javax.inject.Inject
import loggers.MathBotLogger
import model.PlayerTokenModel
import model.models._
import play.api.Environment
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.Future
import scala.concurrent.duration._

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
  {
    def isSuccess() = programState == "success"
    def isFailure() = programState == "failure"
  }

  object ClientFrame {
    def apply(frame: Frame): ClientFrame = ClientFrame(frame, "running")

    def success(frame: Frame): ClientFrame = ClientFrame(frame, "success")

    def failure(frame: Frame): ClientFrame = ClientFrame(frame, "failure")

    def apply(frame: Frame, programState: String): ClientFrame =
      ClientFrame(ClientRobotState(frame), programState)
  }

  case class ClientResponse(frames : List[ClientFrame] = List.empty[ClientFrame], problem : Option[Problem] = None, halted : Option[Boolean] = None, error : Option[String] = None)

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
        SocketRequestConvertFlow()
          .via(
          ActorFlow.actorRef(
          out =>
            CompilerActor.props(out,
                                URLDecoder.decode(encodedTokenId, "UTF-8"),
                                reactiveMongoApi,
                                statsActor,
                                levelActor,
                                mathBotLogger)
        )).via(
          SocketResponseConvertFlow()
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

    request.body.asJson match {
      case Some(json) =>
        val sr = SocketRequestConvertFlow.jsonToCompilerCommand(json)

        val compilerProps =
          CompilerActor.props(fakeActor,
            URLDecoder.decode(encodedTokenId, "UTF-8"),
            reactiveMongoApi,
            statsActor,
            levelActor,
            mathBotLogger)
        val compiler = system.actorOf(compilerProps)

        (compiler ? sr).map(SocketResponseConvertFlow.compilerResponseToJson)
          .map(Ok(_))
      case _ =>
        Future(NoContent)
    }




  }
}


