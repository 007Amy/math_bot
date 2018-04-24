package model.models

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads}

object ToolList {
  implicit val jsonFormat = Json.format[ToolList]

  implicit val toolListReads: Reads[ToolList] = (
    (JsPath \ "1").read[Tool] and
    (JsPath \ "10").read[Tool] and
    (JsPath \ "100").read[Tool] and
    (JsPath \ "1000").read[Tool] and
    (JsPath \ "10000").read[Tool]
  )(ToolList.apply _)
}

case class ToolList(kitty: Tool = Tool("1"),
                    ten: Tool = Tool("10"),
                    oneHundred: Tool = Tool("100"),
                    oneThousand: Tool = Tool("1000"),
                    tenThousand: Tool = Tool("10000"))
