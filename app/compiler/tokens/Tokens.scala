package compiler.tokens

import compiler.operations.Operation

class TokenizedInstruction(val token: Token, val color: String = "default", var operation: Option[Operation] = None) {
}

object TokenizedInstruction {
  def apply(token: Token, color: String = "default", operation: Option[Operation] = None) = new TokenizedInstruction(token, color, operation)

  def unapply(arg: TokenizedInstruction): Option[(Token, String, Option[Operation])] = Some((arg.token, arg.color, arg.operation))
}
