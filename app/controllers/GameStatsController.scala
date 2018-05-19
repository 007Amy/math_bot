package controllers

import java.net.URLDecoder
import javax.inject.Singleton

import actors.GameStatsActor.GetTokenCount
import actors.{GameStatsActor, LevelGenerationActor}
import actors.LevelGenerationActor.{ActorFailed, GetLevel, GetStep}
import actors.messages._
import akka.actor.ActorSystem

import scala.concurrent.duration._
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.ReactiveMongoApi
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import loggers.MathBotLogger
import play.api.Environment
import play.api.libs.json.{JsString, Json}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class GameStatsController @Inject()(system: ActorSystem,
                                    val reactiveMongoApi: ReactiveMongoApi,
                                    logger: MathBotLogger,
                                    environment: Environment)
    extends Controller {
  implicit val timeout: Timeout = 5000.minutes

  val gameStatsActor =
    system.actorOf(GameStatsActor.props(reactiveMongoApi, logger, environment), "game-stats-actor")

  def getCount = Action.async { implicit request =>
    (gameStatsActor ? GetTokenCount).mapTo[Either[Int, ActorFailed]].map {
      case Left(count) =>
        Ok(s"User token count: $count")
      case Right(actorFailed) => BadRequest(actorFailed.msg)
    }
  }
}
