package actors.messages

import actors.LevelGenerationActor.createdIdGen
import actors.messages.PreparedStepData.InitialRobotState
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
    initialRobotState: InitialRobotState,
    stagedEnabled: Boolean,
    activeEnabled: Boolean,
    lambdas: ResponseLambdas,
    toolList: ToolList,
    specialParameters: List[String],
    problem: Problem,
    prevStep: String,
    nextStep: String,
    initFocus: List[String],
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
    initialRobotState = setInitialRobot(rawStepData),
    stagedEnabled = rawStepData.stagedEnabled,
    activeEnabled = rawStepData.activeEnabled,
    lambdas = ResponseLambdas(prepareLambdas(playerToken, rawStepData)),
    toolList = ToolList(),
    specialParameters = rawStepData.specialParameters,
    problem = problemGen(rawStepData),
    prevStep = rawStepData.prevStep,
    nextStep = rawStepData.nextStep,
    initFocus = createInitFocus(rawStepData.initFocus),
    stepControl = new StepControl(rawStepData, prepareLambdas(playerToken, rawStepData))
  )

  case class InitialRobotState(location: Map[String, Int], orientation: String, holding: List[String])

  import model.DefaultCommands._

  def findRobotCoords(grid: List[String], coords: Map[String, Int] = Map("x" -> 0, "y" -> 0)): Map[String, Int] =
    grid match {
      case r :: _ if r contains "(R)" => Map("x" -> coords("x"), "y" -> prepRow(r).indexOf("(R)"))
      case _ :: rest => findRobotCoords(rest, Map("x" -> (coords("x") + 1), "y" -> 0))
    }

  def setInitialRobot(rawStepData: RawStepData): InitialRobotState =
    InitialRobotState(location = findRobotCoords(rawStepData.gridMap),
                      orientation = rawStepData.robotOrientation.toString,
                      holding = List.empty[String])

  def createInitFocus(initFocus: List[String]): List[String] = initFocus.map { a =>
    cmds.find(_.name.getOrElse("") == a) match {
      case Some(token) => token.created_id
      case None =>
        a match {
          case a if a == "open-staged" => "open-staged"
          case a => createdIdGen(a)
        }
    }
  }

  def problemGen(rawStepData: RawStepData): Problem = makeProblem(rawStepData.problem)

  def parseCamelCase(camelStr: String): String = camelStr.toList match {
    case Nil => camelStr.toString
    case l :: rest =>
      if (l.isUpper) " " + l.toString + parseCamelCase(rest.mkString(""))
      else l.toString + parseCamelCase(rest.mkString(""))
  }

  def makeDescription(rawStepData: RawStepData): String = {
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
  }

  private def prepRow(row: String): List[String] = row.split(" ").toList.filterNot(_ == "")

  def buildGrid(gridMap: List[String]): List[List[GridPart]] = {
    gridMap map { row =>
      prepRow(row) map { key =>
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

  implicit val initialRobotStateWrites: Writes[InitialRobotState] = (
    (JsPath \ "location").write[Map[String, Int]] and
    (JsPath \ "orientation").write[String] and
    (JsPath \ "holding").write[List[String]]
  )(unlift(InitialRobotState.unapply))

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
    (JsPath \ "initialRobotState").write[InitialRobotState] and
    (JsPath \ "stagedEnabled").write[Boolean] and
    (JsPath \ "activeEnabled").write[Boolean] and
    (JsPath \ "lambdas").write[ResponseLambdas] and
    (JsPath \ "toolList").write[ToolList] and
    (JsPath \ "specialParameters").write[List[String]] and
    (JsPath \ "problem").write[Problem] and
    (JsPath \ "prevStep").write[String] and
    (JsPath \ "nextStep").write[String] and
    (JsPath \ "initFocus").write[List[String]] and
    OWrites[StepControl](_ => Json.obj())
  )(unlift(PreparedStepData.unapply))

  implicit val stepDataFormat: Format[PreparedStepData] =
    Format(stepDataReads, stepDataWrites)
}
