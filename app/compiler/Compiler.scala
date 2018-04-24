package compiler

import compiler.operations._
import compiler.tokens._
import model.CommandIds
import model.models.{FuncToken, GridMap, GridPart, Problem}
import play.api.libs.json._

import scala.collection.mutable

object Compiler {
  def compile() = ???

  def cellTypeFromName(name: String) = name match {
    case "empty space floor" => CellType.EmptySpace
    case "empty space" => CellType.EmptySpace
    case "final answer" => CellType.FinalAnswer
    case "wall" => CellType.Wall
  }

  def processBoard(grid: GridMap): Option[Grid] = {
    var robotLocation = (0, 0)

    val convertedToElements = grid.gMap
      .zip(Stream.from(0))
      .map(v => (v._2, v._1.zip(Stream.from(0))))
      .flatMap(
        v =>
          v._2.map(
            u =>
              (v._1, u._2) -> (
                u._1 match {
                  case GridPart(name, _, _) if name == "final answer" => Some(Cell(CellType.FinalAnswer))
                  case GridPart(name, robotSpot, tools) if name == "empty space" && !robotSpot && tools.isEmpty => None
                  case GridPart(_, robotSpot, _) if robotSpot =>
                    robotLocation = (v._1, u._2) // Just for the side effect
                    None
                  case GridPart(name, _, tools) if name == "empty space" && tools.nonEmpty =>
                    Some(
                      Cell(
                        cellTypeFromName(name),
                        tools.map(tool => Element(tool.original, tool.name, tool.image, tool.color, tool.value))
                      )
                    )
                  case GridPart(name, _, _) if name == "wall" => Some(Cell(CellType.Wall))
                }
            )
        )
      )
      .flatMap(cl => cl._2.map(c => cl._1 -> c))

    Some(
      Grid(
        Point(robotLocation),
        grid.robotOrientation,
        convertedToElements.toMap
      )
    )
  }

  def processBoard(jBoard: JsArray): Option[Grid] = {
    val convertedToElements = jBoard.value map { row =>
      row.asInstanceOf[JsArray].value map { cell =>
        val f = cell.asInstanceOf[JsObject].value
        (
          f.get("robotSpot").exists(_.asInstanceOf[JsBoolean].value),
          f.get("tools").map(_.asInstanceOf[JsArray].value) map { tools =>
            tools.map(_.asInstanceOf[JsObject]) map { v =>
              Element(
                v.value.get("original").exists(_.asInstanceOf[JsBoolean].value),
                v.value.get("name").map(_.asInstanceOf[JsString].value).get,
                v.value.get("image").map(_.asInstanceOf[JsString].value).get,
                v.value.get("color").map(_.asInstanceOf[JsString].value).get,
                v.value.get("value").map(_.asInstanceOf[JsNumber].value.toInt).get
              )
            }
          },
          f.get("name").map(_.asInstanceOf[JsString].value).getOrElse("empty space")
        )
      } zip Stream.from(0)
    } zip Stream.from(0) flatMap (v => v._1.map(u => (v._2, u._2) -> u._1))

    def isNonEmptyCell(cell: ((Int, Int), (Boolean, Option[Seq[Element]], String))): Boolean = cell match {
      case ((_, _), (_, Some(head :: tails), _)) => true
      case ((_, _), (true, _, _)) => true
      case ((_, _), (_, _, "final answer")) => true
      case _ => false
    }

    convertedToElements.find(_._2._1).map(_._1) map { robotLocation =>
      Grid(
        Point(robotLocation),
        "0",
        convertedToElements
          .filter(isNonEmptyCell)
          .map(v => v._1 -> Cell(cellType = cellTypeFromName(v._2._3), contents = v._2._2.get.toList))
          .toMap
      )
    }
  }

  def compile(script: JsValue,
              main: FuncToken,
              funcs: List[FuncToken],
              commands: List[FuncToken],
              grid: GridMap,
              problem: Problem): Option[GridAndProgram] = {
    val tokenToOps = funcs.map(token => (token.created_id, (token, Option.empty[UserFunction]))).toMap
    val firstPass = fixReferences(convertToOps(main, tokenToOps, commands))
    processBoard(grid).map(g => GridAndProgram(g, new Program(firstPass._1.operations), problem))
  }

  def fixReferences(firstPass: (UserFunction, Map[String, (FuncToken, Option[UserFunction])])) = {

    for {
      f <- firstPass._2.values.flatMap(o => o._2)
    } yield {
      f.operations = f.operations.map {
        case funcRef: UserFunctionRef => firstPass._2(funcRef.created_id)._2.get
        case other => other
      }
    }

    firstPass._1.operations = firstPass._1.operations.map {
      case funcRef: UserFunctionRef => firstPass._2(funcRef.created_id)._2.get
      case other => other
    }
    firstPass
  }

  def convertToOps(main: FuncToken,
                   funcs: Map[String, (FuncToken, Option[UserFunction])],
                   commands: List[FuncToken]): (UserFunction, Map[String, (FuncToken, Option[UserFunction])]) = {
    var newFuncs = funcs
    (
      new UserFunction(
        (for {
          token <- main.func.getOrElse(List.empty[FuncToken])
        } yield {
          token.created_id match {
            case id if commands.map(t => t.created_id).contains(id) =>
              commands.find(c => c.created_id == id).map(c => c.commandId).getOrElse("unknown") match {
                case Some(CommandIds.changeRobotDirection) => ChangeRobotDirection(token.color)
                case Some(CommandIds.moveRobotForwardOneSpot) => MoveRobotForwardOneSpot(token.color)
                case Some(CommandIds.setItemDown) => SetItemDown(token.color)
                case Some(CommandIds.pickUpItem) => PickUpItem(token.color)
                case _ => NoOperation()
              }
            case id if funcs.contains(id) => {
              val f = funcs(id)
              // updating color to user selected color for this instance
              val colorUpdatedF = (f._1.copy(color = token.color), f._2)
              colorUpdatedF._2 match {
                case None =>
                  newFuncs =
                    newFuncs.updated(id, (colorUpdatedF._1, Some(new UserFunctionRef(id, colorUpdatedF._1.color))))
                  val funcOperation = convertToOps(colorUpdatedF._1, newFuncs, commands)
                  newFuncs = funcOperation._2.updated(id, (colorUpdatedF._1, Some(funcOperation._1)))
                  funcOperation._1
                case Some(op) => op
              }
            }
            case _ => NoOperation()
          }
        }).toSeq,
        main.color
      ),
      newFuncs
    )
  }
}
