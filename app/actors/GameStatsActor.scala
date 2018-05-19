package actors

import actors.LevelGenerationActor.{makeQtyUnlimited, ActorFailed}
import actors.messages.{PreparedStepData, RawLevelData, ResponsePlayerToken}
import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.pipe
import loggers.MathBotLogger
import model.{DefaultCommands, PlayerTokenModel}
import model.models._
import play.api.Environment
import play.api.libs.json.{JsPath, JsValue, Json, Reads}
import play.modules.reactivemongo.ReactiveMongoApi
import play.api.libs.functional.syntax._

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
