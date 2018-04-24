package model.models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class PlayerToken(
    token_id: String,
    lambdas: Option[Lambdas] = None,
    stats: Option[Stats] = None,
    randomImages: Option[List[String]] = None
)

object PlayerToken {
  val tokenIdField = "token_id"
  val lambdas = "lambdas"
  val statsField = "stats"
  val randomImagesField = "randomImages"

  implicit val jsonFormat = Json.format[PlayerToken]

  implicit val tokenReads: Reads[PlayerToken] = (
    (JsPath \ PlayerToken.tokenIdField).read[String] and
    (JsPath \ PlayerToken.lambdas).readNullable[Lambdas] and
    (JsPath \ PlayerToken.statsField).readNullable[Stats] and
    (JsPath \ PlayerToken.randomImagesField).readNullable[List[String]]
  )(PlayerToken.apply _)
}
