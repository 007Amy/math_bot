package actors.messages

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class RawLevelData(level: String,
                        prevLevel: String,
                        nextLevel: String,
                        show: Boolean,
                        steps: Map[String, RawStepData])

object RawLevelData {
  val levelDataReads: Reads[RawLevelData] = (
    (JsPath \ "level").read[String] and
    (JsPath \ "prevLevel").read[String] and
    (JsPath \ "nextLevel").read[String] and
    (JsPath \ "show").read[Boolean] and
    (JsPath \ "steps").read[Map[String, RawStepData]]
  )(RawLevelData.apply _)

  val levelDataWrites: Writes[RawLevelData] = (
    (JsPath \ "level").write[String] and
    (JsPath \ "prevLevel").write[String] and
    (JsPath \ "nextLevel").write[String] and
    (JsPath \ "show").write[Boolean] and
    (JsPath \ "steps").write[Map[String, RawStepData]]
  )(unlift(RawLevelData.unapply))

  implicit val levelDataFormat: Format[RawLevelData] =
    Format(levelDataReads, levelDataWrites)
}
