package model.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Json, Reads}

case class StepToken(
    name: String,
    timesPlayed: Int = 0,
    stars: Int = 0,
    active: Boolean = false,
    prevStep: String = "None",
    nextStep: String,
    prevLevel: String = "None",
    nextLevel: String = "None"
)

object StepToken {
  implicit val jsonFormat = Json.format[StepToken]

  implicit val stepTokenReads: Reads[StepToken] = (
    (JsPath \ "name").read[String] and
    (JsPath \ "timesPlayed").read[Int] and
    (JsPath \ "stars").read[Int] and
    (JsPath \ "active").read[Boolean] and
    (JsPath \ "prevStep").read[String] and
    (JsPath \ "nextStep").read[String] and
    (JsPath \ "prevLevel").read[String] and
    (JsPath \ "nextLevel").read[String]
  )(StepToken.apply _)
}
