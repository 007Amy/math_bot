package actors.messages

class CompilerHalt()

object CompilerHalt {
  def apply() = new CompilerHalt
}