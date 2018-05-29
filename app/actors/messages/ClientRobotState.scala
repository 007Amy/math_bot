package actors.messages

import compiler.Point
import compiler.processor.{AnimationType, Frame}
import controllers.MathBotCompiler.ClientGrid

case class ClientRobotState(location: Point,
                            orientation: String,
                            holding: List[String],
                            animation: Option[AnimationType.Value],
                            grid: Option[ClientGrid])

object ClientRobotState {
  def apply(frame: Frame): ClientRobotState = new ClientRobotState(
    location = frame.robotLocation.map(l => Point(l.x, l.y)).getOrElse(Point(0, 0)),
    orientation = frame.robotLocation.map(l => l.orientation).getOrElse("0"),
    animation = frame.register.animation,
    grid = Some(ClientGrid(frame.board)),
    holding = frame.register.holdingCell.contents.map(v => v.image)
  )

  def apply(location: Point, orientation: String, holding: List[String]): ClientRobotState =
    new ClientRobotState(location, orientation, holding, None, None)

  def toTuple(state: ClientRobotState) =
    Some((state.location.x, state.location.y, state.orientation, state.animation.map(a => a.toString)))

}
