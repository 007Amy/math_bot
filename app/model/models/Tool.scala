package model.models

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads}

object Tool {
  implicit val jsonFormat = Json.format[Tool]

  implicit val toolReads: Reads[Tool] = (
    (JsPath \ "original").read[Boolean] and
    (JsPath \ "name").read[String] and
    (JsPath \ "image").read[String] and
    (JsPath \ "value").read[Int] and
    (JsPath \ "color").read[String]
  )(Tool(_, _, _, _, _))

  def apply(toolName: String, original: Option[Boolean]): Tool = toolName match {
    case "1" => Tool(original = original.getOrElse(true), "kitty", "kitty", 1, "blue")
    case "10" => Tool(original = original.getOrElse(true), "ten", "ten", 10, "purple")
    case "100" => Tool(original = original.getOrElse(true), "one-hundred", "oneHundred", 100, "green")
    case "1000" => Tool(original = original.getOrElse(true), "one-thousand", "oneThousand", 1000, "pink")
    case "10000" => Tool(original = original.getOrElse(true), "ten-thousand", "tenThousand", 10000, "red")
    case _ => throw new Exception(s"$toolName is not a valid tool name.")
  }
}

case class Tool(original: Boolean, name: String, image: String, value: Int, color: String)
