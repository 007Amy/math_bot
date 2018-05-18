package actors.messages

import model.models.Problem

case class CompilerStep(steps : Int, problem : Problem)
