package compiler.processor

import compiler.{Grid, GridAndProgram}
import compiler.operations._

class Processor(val initialGridAndProgram: GridAndProgram) {

  private class ProcessorState(
      var currentRegister: Register,
      var currentGrid: Grid
  )

  def execute(): Stream[Frame] = {
    val state = new ProcessorState(Register(), initialGridAndProgram.grid)
    execute(state, initialGridAndProgram.program)
  }

  private def passColorCheck(operation: Operation, state: ProcessorState): Boolean =
    (operation.getColor(), state.currentRegister.holdingCell.contents.headOption.map(v => v.color)) match {
      case (Some("grey"), Some(_)) => true
      case (Some(opColor), Some(botColor)) => opColor == botColor
      case (Some(opColor), None) => false
      case (None, _) => true
    }

  private def execute(state: ProcessorState, operation: Operation): Stream[Frame] =
    if (passColorCheck(operation, state))
      operation match {
        case Program(operations) =>
          def rest =
            (operations ++: Stream.empty[Operation]).flatMap(execute(state, _))

          Stream.cons(process(state, operation), rest)
        case UserFunction(operations) =>
          def rest =
            (operations ++: Stream.empty[Operation]).flatMap(execute(state, _))

          Stream.cons(process(state, operation), rest)
        case _ =>
          process(state, operation) #:: Stream.empty[Frame]
      } else
      Stream.empty[Frame]

  private def process(state: ProcessorState, operation: Operation): Frame = operation match {

    case Program(_) =>
      state.currentRegister = Register()
      Frame(operation, state.currentRegister, state.currentGrid)

    case PickUpItem(color) =>
      val boardAndBox = state.currentGrid.pickupItem()
      state.currentRegister =
        boardAndBox._2.map(box => state.currentRegister.push(box)).getOrElse(state.currentRegister).clearAnimation()
      state.currentGrid = boardAndBox._1
      Frame(operation, state.currentRegister, boardAndBox._1, Some(state.currentGrid.getRobotLocation))

    case SetItemDown(color) =>
      val registerAndBox = state.currentRegister.pop()
      state.currentGrid = registerAndBox.map(v => state.currentGrid.setItemDown(v._2)).getOrElse(state.currentGrid)
      state.currentRegister = registerAndBox.map(_._1).getOrElse(state.currentRegister).clearAnimation()
      Frame(operation, state.currentRegister, state.currentGrid, Some(state.currentGrid.getRobotLocation))

    case ChangeRobotDirection(color) =>
      state.currentGrid = state.currentGrid.changeDirection()
      state.currentRegister = state.currentRegister.clearAnimation()
      Frame(operation, state.currentRegister, state.currentGrid, Some(state.currentGrid.getRobotLocation))

    case MoveRobotForwardOneSpot(color) =>
      state.currentGrid.moveRobotForwardOneSpot() match {
        case Some(grid) => {
          state.currentGrid = grid
          state.currentRegister = state.currentRegister.clearAnimation()
          Frame(operation, state.currentRegister, state.currentGrid, Some(state.currentGrid.getRobotLocation))
        }
        case None => {
          state.currentRegister = state.currentRegister.copy(animation = Some(AnimationType.Bumped))
          Frame(operation, state.currentRegister, state.currentGrid, Some(state.currentGrid.getRobotLocation))
        }
      }

    case _ => Frame(operation, state.currentRegister, state.currentGrid)
  }
}

object Processor {
  def apply(initialGridAndProgram: GridAndProgram): Processor = new Processor(initialGridAndProgram)
}
