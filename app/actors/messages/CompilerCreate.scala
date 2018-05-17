package actors.messages

import model.models.Problem

case class CompilerCreate(steps : Int, problem : Problem)
