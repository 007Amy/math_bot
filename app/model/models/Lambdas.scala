package model.models

import model.DefaultCommands.{cmds, funcs, main}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads}

case class Lambdas(
    main: FuncToken = main,
    defaultFuncs: Option[List[FuncToken]] = Some(funcs),
    stagedFuncs: List[FuncToken] = List.empty[FuncToken],
    cmds: List[FuncToken] = cmds,
    activeFuncs: List[FuncToken] = List.empty[FuncToken]
)

object Lambdas {

  implicit val jsonFormat = Json.format[Lambdas]

  implicit val commandReads: Reads[Lambdas] = (
    (JsPath \ "main").read[FuncToken] and
    (JsPath \ "defaultFuncs").readNullable[List[FuncToken]] and
    (JsPath \ "stagedFuncs").read[List[FuncToken]] and
    (JsPath \ "cmds").read[List[FuncToken]] and
    (JsPath \ "activeFuncs").read[List[FuncToken]]
  )(Lambdas.apply _)
}
