package actors

import akka.actor.{ Actor, ActorRef, Props }
import loggers.MathBotLogger
import play.api.libs.json.JsString

class SameOriginFailedActor(out: ActorRef, logger: MathBotLogger) extends Actor {

  val msg = "Failed same origin check."

  override def receive: Receive = {
    case _ =>
      out ! JsString(msg)
  }
}

object SameOriginFailedActor {
  def props(out: ActorRef, logger: MathBotLogger) = Props(new SameOriginFailedActor(out, logger))
}