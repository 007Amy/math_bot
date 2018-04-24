package actors.messages

import model.DefaultCommands.main
import model.models.{FuncToken, Lambdas}
import play.api.libs.json.Json

object ResponseLambdas {
  def apply(lambdas: Lambdas): ResponseLambdas =
    new ResponseLambdas(main = lambdas.main,
                        stagedFuncs = lambdas.stagedFuncs,
                        defaultFuncs = lambdas.defaultFuncs.get,
                        cmds = lambdas.cmds,
                        activeFuncs = lambdas.activeFuncs)

  implicit val jsonFormat = Json.format[ResponseLambdas]
}

case class ResponseLambdas(
    main: FuncToken = main,
    stagedFuncs: List[FuncToken],
    defaultFuncs: List[FuncToken], // REMEMBER TO REMOVE!!!
    cmds: List[FuncToken],
    activeFuncs: List[FuncToken]
)
