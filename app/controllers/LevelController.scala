package controllers

import java.net.URLDecoder
import javax.inject.Singleton

import actors.LevelGenerationActor
import actors.LevelGenerationActor.{GetLevel, GetStep}
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
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class LevelController @Inject()(system: ActorSystem,
                                val reactiveMongoApi: ReactiveMongoApi,
                                logger: MathBotLogger,
                                environment: Environment)
    extends Controller {

  implicit val timeout: Timeout = 5000.minutes

  val levelActor = system.actorOf(LevelGenerationActor.props(reactiveMongoApi, logger, environment), "level-actor")

  def getStep(level: String, step: String, encodedTokenId: Option[String]) = Action.async { implicit request =>
    (levelActor ? GetStep(level, step, encodedTokenId.map(URLDecoder.decode(_, "UTF-8"))))
      .mapTo[Either[PreparedStepData, LevelGenerationActor.ActorFailed]]
      .map {
        case Left(preparedStepData) => Ok(Json.toJson(preparedStepData))
        case Right(invalidJson) => BadRequest(invalidJson.msg)
      }
  }

  def getLevel(level: String, encodedTokenId: Option[String]) = Action.async { implicit request =>
    (levelActor ? GetLevel(level, encodedTokenId.map(URLDecoder.decode(_, "UTF-8"))))
      .mapTo[Either[RawLevelData, LevelGenerationActor.ActorFailed]]
      .map {
        case Left(rawLevelData) => Ok(Json.toJson(rawLevelData))
        case Right(invalidJson) => BadRequest(invalidJson.msg)
      }
  }
}
