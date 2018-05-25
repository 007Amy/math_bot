package actors

import actors.messages._
import akka.NotUsed
import akka.stream.scaladsl.Flow
import compiler.Point
import compiler.processor.AnimationType
import controllers.MathBotCompiler.{ClientFrame, _}
import model.models.Problem
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsString, JsValue, Writes, _}

object SocketResponseConvertFlow {

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

  implicit val clientFrameWrites = Json.writes[ClientFrame]

  implicit val problemWrites = new Writes[Problem] {
    def writes(o: Problem) = JsString(o.encryptedProblem)
  }

  implicit val clientResponseFormat = Json.writes[ClientResponse]

  def compilerResponseToJson(msg: Any): JsValue = {
    val cr = msg match {
      case CompilerOutput(frames, problem) => ClientResponse(frames, Some(problem))
      case _: CompilerHalted => ClientResponse(halted = Some(true))
      case ActorFailed(msg) => ClientResponse(error = Some(msg))
      case _ => ClientResponse(error = Some("Unknown response from compiler"))
    }
    Json.toJson[ClientResponse](cr)
  }

  def apply(): Flow[Any, JsValue, NotUsed] = Flow[Any].map(compilerResponseToJson)

}
