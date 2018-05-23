package actors.messages

import model.models.Problem

case class CompilerContinue(steps : Int, problem : Problem)
