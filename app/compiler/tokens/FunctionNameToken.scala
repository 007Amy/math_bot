package compiler.tokens

class FunctionNameToken(var name: String) extends Token {
  id = name
}

object FunctionNameToken {
  def apply(name: String): FunctionNameToken = new FunctionNameToken(name)

  def unapply(arg: FunctionNameToken): Option[String] = Some(arg.name)
}