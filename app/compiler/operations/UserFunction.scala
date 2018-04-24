package compiler.operations

class UserFunction(var operations: Seq[Operation] = Seq.empty[Operation], val color: String) extends OperationWithColor {
  override def toString = s"UserFunction($color)"
}

object UserFunction {
  def unapply(arg: UserFunction): Option[Seq[Operation]] = Some(arg.operations)
}
