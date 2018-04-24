package controllers

import java.net.URLDecoder
import javax.inject.Inject

import actors.LevelGenerationActor.ActorFailed
import actors.PlayerActor
import actors.PlayerActor._
import actors.messages.ResponsePlayerToken
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import loggers.MathBotLogger
import model.PlayerTokenModel
import play.api.Environment
import play.api.mvc._
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.duration._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

class PlayerController @Inject()(system: ActorSystem,
                                 val reactiveMongoApi: ReactiveMongoApi,
                                 logger: MathBotLogger,
                                 environment: Environment)
    extends Controller
    with PlayerTokenModel {

  implicit val timeout: Timeout = 5000.minutes

  val playerActor = system.actorOf(PlayerActor.props(system, reactiveMongoApi, logger, environment), "player-actor")

  def addToken(): Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue] =>
    (playerActor ? AddToken(request.body)).mapTo[Either[ResponsePlayerToken, ActorFailed]].map {
      case Left(responsePlayerToken) =>
        Ok(Json.toJson(responsePlayerToken))
      case Right(invalidJson) => BadRequest(invalidJson.msg)
    }
  }

  def editLambdas(): Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue] =>
    (playerActor ? EditLambdas(request.body)).mapTo[Either[PreparedLambdasToken, ActorFailed]].map {
      case Left(preparedLambdasToken) => Ok(Json.toJson(preparedLambdasToken.lambdas))
      case Right(invalidJson) => BadRequest(invalidJson.msg)
    }
  }

  def activateFunction(encodedTokenId: String, stagedIndex: String, activeIndex: String): Action[AnyContent] =
    Action.async { implicit request: Request[AnyContent] =>
      (playerActor ? ActivateFunc(URLDecoder.decode(encodedTokenId, "UTF-8"), stagedIndex, activeIndex))
        .mapTo[Either[PreparedLambdasToken, ActorFailed]]
        .map {
          case Left(preparedLambdasToken) => Ok(Json.toJson(preparedLambdasToken.lambdas))
          case Right(invalidJson) => BadRequest(invalidJson.msg)
        }
    }
}
