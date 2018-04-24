package compiler.tokens

class ProgramToken(f: Seq[TokenizedInstruction]) extends Token {
  this.func = f
}

object ProgramToken {
  def apply(func: Seq[TokenizedInstruction]): ProgramToken = new ProgramToken(func)

  def unapply(arg: ProgramToken): Option[Seq[TokenizedInstruction]] = if (arg.func.isEmpty) Option.empty[Seq[TokenizedInstruction]] else Some(arg.func)
}