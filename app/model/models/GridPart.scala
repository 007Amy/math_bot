package model.models

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads}

object GridPart {
  implicit val jsonFormat = Json.format[GridPart]

  implicit val gridPartReads: Reads[GridPart] = (
    (JsPath \ "name").read[String] and
    (JsPath \ "robotSpot").read[Boolean] and
    (JsPath \ "tools").read[List[Tool]]
  )(GridPart(_, _, _))

  def apply(key: String): GridPart = key match {
    case "(R)" => new GridPart(name = "empty space", robotSpot = true)
    case "($)" => new GridPart("final answer")
    case "|E|" => new GridPart("empty space")
    case "|W|" => new GridPart("wall")
    case "[1]" => new GridPart(name = "empty space", tools = List(Tool("1")))
    case "[10]" => new GridPart(name = "empty space", tools = List(Tool("10")))
    case "[100]" => new GridPart(name = "empty space", tools = List(Tool("100")))
    case "[1g]" => new GridPart(name = "empty space", tools = List(Tool("1000")))
    case "[10g]" => new GridPart(name = "empty space", tools = List(Tool("10000")))
    case k if k.matches("[TS]") => new GridPart(name = "empty space") // WILL IMPLEMENT LATER
    case _ => new GridPart("empty space")
  }
}

case class GridPart(name: String, robotSpot: Boolean = false, tools: List[Tool] = List.empty[Tool])
