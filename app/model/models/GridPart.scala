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

  def setTools(str: String): List[Tool] = {
    str
      .replaceFirst("TS", "")
      .replaceAll("\\[", "")
      .replaceAll("\\]", "")
      .replaceAll("\\(", "")
      .replaceAll("\\)", "")
      .split(",")
      .toList
      .map {
        case "10g" => Tool(toolName = "10000", original = Some(false))
        case "1g" => Tool(toolName = "1000", original = Some(false))
        case "100" => Tool(toolName = "100", original = Some(false))
        case "10" => Tool(toolName = "10", original = Some(false))
        case "1" => Tool(toolName = "1", original = Some(false))
      }
  }

  def apply(key: String): GridPart = key match {
    case "(R)" => new GridPart(name = "empty space", robotSpot = true)
    case "($)" => new GridPart("final answer")
    case "|E|" => new GridPart("empty space")
    case "|W|" => new GridPart("wall")
    case "[1]" => new GridPart(name = "empty space", tools = List(Tool("1", None)))
    case "[10]" => new GridPart(name = "empty space", tools = List(Tool("10", None)))
    case "[100]" => new GridPart(name = "empty space", tools = List(Tool("100", None)))
    case "[1g]" => new GridPart(name = "empty space", tools = List(Tool("1000", None)))
    case "[10g]" => new GridPart(name = "empty space", tools = List(Tool("10000", None)))
    case k if k contains "[TS" => new GridPart(name = "empty space", tools = setTools(k))
    case _ => new GridPart("empty space")
  }
}

case class GridPart(name: String, robotSpot: Boolean = false, tools: List[Tool] = List.empty[Tool])
