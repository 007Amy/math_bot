package actors

import actors.messages.ActorFailed
import akka.actor.{Actor, Props}

import akka.pattern.pipe
import loggers.MathBotLogger
import model.PlayerTokenModel
import play.api.Environment
import play.modules.reactivemongo.ReactiveMongoApi

object GameStatsActor {
  final case class GetTokenCount()

  final case class GameStatsFinished(userCount: Option[Int])

  def props(reactiveMongoApi: ReactiveMongoApi, logger: MathBotLogger, environment: Environment) =
    Props(new GameStatsActor()(reactiveMongoApi, logger, environment))
}

class GameStatsActor()(val reactiveMongoApi: ReactiveMongoApi, logger: MathBotLogger, environment: Environment)
    extends Actor
    with PlayerTokenModel {
  import GameStatsActor._
  import context.dispatcher

  private final val className = "GameStatsActor"

  override def receive: Receive = {
    case GetTokenCount =>
      getTokenCount
        .map { count =>
          GameStatsFinished(userCount = Some(count))
        }
        .pipeTo(self)(sender)
    case GameStatsFinished(userCount) => sender ! Left(userCount.getOrElse(0))
    case _ => sender ! Right(ActorFailed("Something went wrong getting game stats"))
  }
}
