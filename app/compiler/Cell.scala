package compiler

case class Cell(cellType: CellType.Value, contents: List[Element] = List.empty[Element]) {
  def pop(): (Option[Cell], Option[Element]) = contents.headOption match {
    case Some(topElement) =>
      if (topElement.original) {
        (Some(this), Some(topElement.copy(original = false)))
      } else {
        contents.tail match {
          case Nil if this.cellType == CellType.EmptySpace => (None, Some(topElement))
          case Nil => (Some(this.copy(contents = List.empty[Element])), Some(topElement))
          case remaining => (Some(this.copy(contents = remaining)), Some(topElement))
        }
      }
    case None => (Some(this), None)
  }

  def push(newElement: Element): Cell = this.copy(contents = newElement :: this.contents)
}

object Cell {
  def setItemDown(cell: Option[Cell], newElement: Element) = cell match {
    case Some(existingCell) => existingCell.push(newElement)
    case None => Cell(CellType.EmptySpace, List(newElement))
  }

  def pickupItem(cell: Option[Cell]): Option[(Option[Cell], Option[Element])] = cell match {
    case Some(existingCell) => Some(existingCell.pop())
    case None => None
  }
}
