package compiler.tokens

class EmptyToken() extends Token

object EmptyToken {
  def apply(): EmptyToken = new EmptyToken()
}