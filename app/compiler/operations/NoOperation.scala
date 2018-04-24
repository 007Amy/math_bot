package compiler.operations

case class NoOperation() extends Operation {
  override def getColor(): Option[String] = None
}
