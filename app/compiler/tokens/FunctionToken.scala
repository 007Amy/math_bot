package compiler.tokens

class FunctionToken(f: Seq[TokenizedInstruction], i: String) extends Token {
  this.func = f
  this.id = i
}

object FunctionToken {
  def apply(func: Seq[TokenizedInstruction], id: String) = new FunctionToken(func, id)

  def unapply(arg: FunctionToken): Option[(Seq[TokenizedInstruction], String)] = Some((arg.func, arg.id))
}