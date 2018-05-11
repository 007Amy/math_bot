package actors.messages

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class RawStepData(
    level: String,
    step: String,
    gridMap: List[String],
    description: String,
    mainMax: Int,
    robotOrientation: Int,
    stagedEnabled: Boolean,
    activeEnabled: Boolean,
    stagedQty: Int,
    assignedStaged: Map[String, String],
    activeQty: Int,
    preBuiltActive: Map[String, List[String]],
    cmdsAvailable: List[String],
    specialParameters: List[String],
    problem: String,
    clearMain: Boolean,
    initFocus: List[String],
    prevStep: String,
    nextStep: String
)

object RawStepData {
  val stepDataReads: Reads[RawStepData] = (
    (JsPath \ "level").read[String] and
    (JsPath \ "step").read[String] and
    (JsPath \ "gridMap").read[List[String]] and
    (JsPath \ "description").read[String] and
    (JsPath \ "mainMax").read[Int] and
    (JsPath \ "robotOrientation").read[Int] and
    (JsPath \ "stagedEnabled").read[Boolean] and
    (JsPath \ "activeEnabled").read[Boolean] and
    (JsPath \ "stagedQty").read[Int] and
    (JsPath \ "assignedStaged").read[Map[String, String]] and
    (JsPath \ "activeQty").read[Int] and
    (JsPath \ "preBuiltActive").read[Map[String, List[String]]] and
    (JsPath \ "cmdsAvailable").read[List[String]] and
    (JsPath \ "specialParameters").read[List[String]] and
    (JsPath \ "problem").read[String] and
    (JsPath \ "clearMain").read[Boolean] and
    (JsPath \ "initFocus").read[List[String]] and
    (JsPath \ "prevStep").read[String] and
    (JsPath \ "nextStep").read[String]
  )(RawStepData.apply _)

  val stepDataWrites: Writes[RawStepData] = (
    (JsPath \ "level").write[String] and
    (JsPath \ "step").write[String] and
    (JsPath \ "gridMap").write[List[String]] and
    (JsPath \ "description").write[String] and
    (JsPath \ "mainMax").write[Int] and
    (JsPath \ "robotOrientation").write[Int] and
    (JsPath \ "stagedEnabled").write[Boolean] and
    (JsPath \ "activeEnabled").write[Boolean] and
    (JsPath \ "stagedQty").write[Int] and
    (JsPath \ "assignedStaged").write[Map[String, String]] and
    (JsPath \ "activeQty").write[Int] and
    (JsPath \ "preBuiltActive").write[Map[String, List[String]]] and
    (JsPath \ "cmdsAvailable").write[List[String]] and
    (JsPath \ "specialParameters").write[List[String]] and
    (JsPath \ "problem").write[String] and
    (JsPath \ "clearMain").write[Boolean] and
    (JsPath \ "initFocus").write[List[String]] and
    (JsPath \ "prevStep").write[String] and
    (JsPath \ "nextStep").write[String]
  )(unlift(RawStepData.unapply))

  implicit val stepDataFormat: Format[RawStepData] =
    Format(stepDataReads, stepDataWrites)
}
