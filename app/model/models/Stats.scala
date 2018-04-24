package model.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads, _}

object Stats {
  val statsReads: Reads[Stats] = (
    (JsPath \ "level").read[String] and
    (JsPath \ "step").read[String] and
    (JsPath \ "gameComplete").readNullable[Boolean] and
    (JsPath \ "levels").read[Map[String, Map[String, StepToken]]]
  )(Stats.apply _)

  val statsWrites: Writes[Stats] = (
    (JsPath \ "level").write[String] and
    (JsPath \ "step").write[String] and
    (JsPath \ "gameComplete").writeNullable[Boolean] and
    (JsPath \ "levels").write[Map[String, Map[String, StepToken]]]
  )(unlift(Stats.unapply))

  implicit val funcTokenFormat: Format[Stats] =
    Format(statsReads, statsWrites)
}

case class Stats(
    level: String,
    step: String,
    gameComplete: Option[Boolean] = Some(false),
    levels: Map[String, Map[String, StepToken]]
)
