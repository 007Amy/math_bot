package compiler.operations

trait OperationWithColor extends Operation {
  val color: String

  def getColor(): Option[String] = if (color == "default") None else Some(color)
}
