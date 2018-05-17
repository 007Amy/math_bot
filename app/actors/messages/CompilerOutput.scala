package actors.messages

import controllers.MathBotCompiler.ClientFrame
import model.models.Problem

case class CompilerOutput(frames : List[ClientFrame], problem: Problem)
