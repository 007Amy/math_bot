package actors.messages

import model.models.Problem

case class CompilerExecute(steps : Int, problem : Problem, exitOnSuccess : Boolean = false)

