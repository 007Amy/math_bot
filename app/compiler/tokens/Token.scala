package compiler.tokens

class Token {
  var id: String = ""
  var func: Seq[TokenizedInstruction] = Seq.empty[TokenizedInstruction]
}
