package compiler.processor

import compiler.{Cell, CellType, Element}

object AnimationType extends Enumeration {
  val Bumped = Value("bumped")
}

case class Register(holdingCell: Cell = Cell(cellType = CellType.RobotHolding),
                    animation: Option[AnimationType.Value] = None) {
  def clearAnimation(): Register = if (animation.isDefined) this.copy(animation = None) else this

  def pop(): Option[(Register, Element)] = holdingCell.pop() match {
    case (Some(newCell), Some(topElement)) => Some((this.copy(holdingCell = newCell), topElement))
    case _ => None
  }

  def push(newElement: Element): Register = this.copy(holdingCell = this.holdingCell.push(newElement))
}
