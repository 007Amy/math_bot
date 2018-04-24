package compiler

import compiler.processor.RobotLocation

object CellType extends Enumeration {
  val EmptySpace, Wall, FinalAnswer, RobotHolding = Value
}

case class Point(x: Int, y: Int) {
  def asTuple = (x, y)
}

object Point {
  def apply(coords: (Int, Int)): Point = Point(coords._1, coords._2)
}

case class Rectangle(upperLeft: Point, lowerRight: Point) {
  def inside(loc: Point): Boolean =
    loc.x >= upperLeft.x && loc.y >= upperLeft.y && loc.x <= lowerRight.x && loc.y <= lowerRight.y
}

case class Grid(
    robotLocation: Point = Point(0, 0),
    robotOrientation: String = "0",
    grid: Map[(Int, Int), Cell] = Map.empty[(Int, Int), Cell],
    gridDimensions: Rectangle = Rectangle(Point(0, 0), Point(4, 9))
) {
  def currentCell() = getCell(robotLocation)

  def getCell(location: Point): Cell = grid.getOrElse(location.asTuple, Cell(CellType.EmptySpace, List.empty[Element]))

  def getRobotLocation: RobotLocation = RobotLocation(robotLocation.x, robotLocation.y, robotOrientation)

  def moveRobotForwardOneSpot(): Option[Grid] = {
    val newLocation = robotOrientation match {
      case "0" => Point(robotLocation.x - 1, robotLocation.y)
      case "90" => Point(robotLocation.x, robotLocation.y + 1)
      case "180" => Point(robotLocation.x + 1, robotLocation.y)
      case "270" => Point(robotLocation.x, robotLocation.y - 1)
    }

    if (gridDimensions.inside(newLocation)) {
      getCell(newLocation) match {
        case Cell(CellType.FinalAnswer, _) => Some(this.copy(robotLocation = newLocation))
        case Cell(CellType.EmptySpace, _) => Some(this.copy(robotLocation = newLocation))
        case Cell(CellType.Wall, _) => None
      }
    } else {
      None
    }
  }

  def changeDirection(): Grid =
    this.copy(robotOrientation = robotOrientation match {
      case "0" => "90"
      case "90" => "180"
      case "180" => "270"
      case "270" => "0"
    })

  // Create a new grid with the new element list at the robot location.  If there is an existing set of elements,
  // the "+" operator replaces them with the new list of elements.
  def updateCurrentCell(cell: Option[Cell]): Grid = cell match {
    case Some(c) => this.copy(grid = this.grid + (robotLocation.asTuple -> c))
    case None => this.copy(grid = this.grid - robotLocation.asTuple)
  }

  def setItemDown(newElement: Element): Grid =
    updateCurrentCell(Some(Cell.setItemDown(grid.get(robotLocation.asTuple), newElement)))

  def pickupItem(): (Grid, Option[Element]) = {
    val existingElementsAtRobotLocation = Cell.pickupItem(grid.get(robotLocation.asTuple))

    existingElementsAtRobotLocation match {
      case Some((cell, Some(element))) => (updateCurrentCell(cell), Some(element))
      case Some((cell, None)) => (updateCurrentCell(cell), None)
      case None => (this, None)
    }
  }
}
