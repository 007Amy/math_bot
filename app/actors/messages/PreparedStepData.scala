package actors.messages

import model.models.{ToolList, _}
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class PreparedStepData(
    tokenId: String,
    level: String,
    step: String,
    gridMap: List[List[GridPart]],
    description: String,
    mainMax: Int,
    robotOrientation: Int,
    stagedEnabled: Boolean,
    activeEnabled: Boolean,
    lambdas: ResponseLambdas,
    toolList: ToolList,
    specialParameters: List[String],
    problem: String,
    prevStep: String,
    nextStep: String,
    stepControl: StepControl
)

object PreparedStepData {

  import model.models.Problem._

  def apply(playerToken: PlayerToken, rawStepData: RawStepData): PreparedStepData = new PreparedStepData(
    tokenId = playerToken.token_id,
    level = rawStepData.level,
    step = rawStepData.step,
    gridMap = buildGrid(rawStepData.gridMap),
    description = makeDescription(rawStepData),
    mainMax = rawStepData.mainMax,
    robotOrientation = rawStepData.robotOrientation,
    stagedEnabled = rawStepData.stagedEnabled,
    activeEnabled = rawStepData.activeEnabled,
    lambdas = ResponseLambdas(prepareLambdas(playerToken, rawStepData)),
    toolList = ToolList(),
    specialParameters = rawStepData.specialParameters,
    problem = problemGen(rawStepData).encryptedProblem,
    prevStep = rawStepData.prevStep,
    nextStep = rawStepData.nextStep,
    stepControl = new StepControl(rawStepData, prepareLambdas(playerToken, rawStepData))
  )

  def problemGen(rawStepData: RawStepData): Problem = makeProblem(rawStepData.problem)

  def parseCamelCase(camelStr: String): String = camelStr.toList match {
    case Nil => camelStr.toString
    case l :: rest =>
      if (l.isUpper) " " + l.toString + parseCamelCase(rest.mkString(""))
      else l.toString + parseCamelCase(rest.mkString(""))
  }

  def makeDescription(rawStepData: RawStepData): String = {
    val taco = rawStepData.description
      .replaceAll("!![P]!!", problemGen(rawStepData).problem)
      .replaceAll("!![S]!!", parseCamelCase(rawStepData.step))
      .replaceAll("!![L]!!", parseCamelCase(rawStepData.level))

    val shell: String =
    "<p>" +
    s"${rawStepData.description
      .split("!!")
      .map {
        case a if a == "[P]" => problemGen(rawStepData).problem
        case a if a == "[S]" => parseCamelCase(rawStepData.step)
        case a if a == "[L]" => parseCamelCase(rawStepData.level)
        case a if a contains "[img]" =>
          a.replace("[img]", "<img ") + " />"
        case a => a
      }
      .mkString(" ")
      .split("\n")
      .mkString(" <br /> ")}" +
    s"</p>"

    val oval = 0

    shell
  }

  def buildGrid(gridMap: List[String]): List[List[GridPart]] = {
    gridMap map { row =>
      val r = row.split(" ").toList
      r map { key =>
        GridPart.apply(key)
      }
    }
  }

  def prepareLambdas(playerToken: PlayerToken, rawStepData: RawStepData): Lambdas = {
    playerToken.lambdas match {
      case Some(lambdas) =>
        val cmds = lambdas.cmds
          .filter(ft => rawStepData.cmdsAvailable.contains(ft.commandId.getOrElse("nothing")))

        val main = lambdas.main.func match {
          case Some(funcs) => lambdas.main.copy(func = Some(funcs.take(rawStepData.mainMax)))
          case None => lambdas.main.copy(func = Some(List.empty[FuncToken]))
        }

        lambdas.copy(activeFuncs = lambdas.activeFuncs, main = main, cmds = cmds)
      case None => Lambdas()
    }
  }

  val stepDataReads: Reads[PreparedStepData] = (
    (JsPath \ "playerToken").read[PlayerToken] and
    (JsPath \ "rawStepData").read[RawStepData]
  )(PreparedStepData(_, _))

  val stepDataWrites: Writes[PreparedStepData] = (
    (JsPath \ "tokenId").write[String] and
    (JsPath \ "level").write[String] and
    (JsPath \ "step").write[String] and
    (JsPath \ "gridMap").write[List[List[GridPart]]] and
    (JsPath \ "description").write[String] and
    (JsPath \ "mainMax").write[Int] and
    (JsPath \ "robotOrientation").write[Int] and
    (JsPath \ "stagedEnabled").write[Boolean] and
    (JsPath \ "activeEnabled").write[Boolean] and
    (JsPath \ "lambdas").write[ResponseLambdas] and
    (JsPath \ "toolList").write[ToolList] and
    (JsPath \ "specialParameters").write[List[String]] and
    (JsPath \ "problem").write[String] and
    (JsPath \ "prevStep").write[String] and
    (JsPath \ "nextStep").write[String] and
    OWrites[StepControl](_ => Json.obj())
  )(unlift(PreparedStepData.unapply))

  implicit val stepDataFormat: Format[PreparedStepData] =
    Format(stepDataReads, stepDataWrites)
}
