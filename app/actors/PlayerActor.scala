package actors

import actors.LevelGenerationActor.makeQtyUnlimited
import actors.PolyfillActor.ApplyPolyfills
import actors.messages.{ActorFailed, PreparedStepData, RawLevelData, ResponsePlayerToken}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.pipe
import akka.pattern.ask
import akka.util.Timeout
import loggers.MathBotLogger
import model.{DefaultCommands, PlayerTokenModel}
import model.models._
import play.api.Environment
import play.api.libs.json.{JsPath, JsValue, Json, Reads}
import play.modules.reactivemongo.ReactiveMongoApi
import play.api.libs.functional.syntax._
import scala.concurrent.duration._

import scala.concurrent.Future

object PlayerActor {

  case class AddToken(body: JsValue)

  case class GetPlayerToken(playerToken: PlayerToken)

  case class GatherLevel(playerToken: PlayerToken)

  case class UpdateFunc(funcToken: FuncToken, playerToken: PlayerToken, `override`: Boolean)

  case class ActivateFunc(tokenId: String, stagedIndex: String, activeIndex: String)

  case class MoveFunc(playerToken: PlayerToken, stagedIndex: String, activeIndex: String)

  case class AddDefaultFuncsField(playerToken: PlayerToken)

  case class AddNamesToCommands(playerToken: PlayerToken)

  case class DedupFunctions(playerToken: PlayerToken)

  object MakeStats {
    def makeStats(levels: Map[String, RawLevelData]): Stats = {
      val firstLevel = levels.find(rld => rld._2.prevLevel == "None" || !levels.isDefinedAt(rld._2.prevLevel)) match {
        case Some((_, rawLevelData)) => rawLevelData.copy(prevLevel = "None")
        case None => throw new Exception("Failed to find first level.")
      }

      val firstStep = firstLevel.steps.find(rsd => rsd._2.prevStep == "None") match {
        case Some((_, rawStepData)) => rawStepData.copy(prevStep = "None")
        case None => throw new Exception("Failed to find first step.")
      }

      val levelStats = levels map { level =>
        (level._1, level._2.steps map { step =>
          (step._1,
           StepToken(
             name = step._2.step,
             prevStep = step._2.prevStep,
             nextStep = step._2.nextStep,
             active = step._2.prevStep == "None" && level._2.prevLevel == "None",
             prevLevel =
               if (step._2.prevStep == "None" && levels.isDefinedAt(level._2.prevLevel))
                 level._2.prevLevel
               else "None",
             nextLevel =
               if (step._2.nextStep == "None" && levels.isDefinedAt(level._2.nextLevel))
                 level._2.nextLevel
               else "None"
           ))
        })
      }

      Stats(
        level = firstLevel.level,
        step = firstStep.step,
        levels = levelStats
      )
    }

    def makeStatsSame(baseStats: Stats, userStats: Stats): Stats = {
      userStats.copy(
        level =
          if (baseStats.levels.isDefinedAt(userStats.level)) userStats.level
          else baseStats.level,
        step =
          if (baseStats.levels
                .isDefinedAt(userStats.level) && baseStats.levels(userStats.level).isDefinedAt(userStats.step))
            userStats.step
          else baseStats.step,
        levels = {
          val filteredLevels = userStats.levels
            .filter(s => baseStats.levels.isDefinedAt(s._1))

          val addedLevels = (filteredLevels.toSeq union baseStats.levels.toSeq).toMap

          addedLevels
            .map {
              l =>
                if (userStats.levels.isDefinedAt(l._1)) {
                  l._1 -> l._2.map {
                    s =>
                      if (userStats.levels(l._1).isDefinedAt(s._1)) {
                        s._1 -> s._2.copy(
                          timesPlayed = userStats.levels(l._1)(s._1).timesPlayed,
                          stars = userStats.levels(l._1)(s._1).stars,
                          active = userStats.levels(l._1)(s._1).active,
                          prevLevel = s._2.prevLevel,
                          nextLevel = s._2.nextLevel,
                          prevStep = s._2.prevStep,
                          nextStep = s._2.nextStep
                        )
                      } else {
                        s._1 -> s._2.copy(active = {
                          filteredLevels(l._1).get(s._2.prevStep) match {
                            case Some(t) => t.active
                            case None => false
                          }
                        })
                      }
                  }
                } else l
            }
            .map { l =>
              l._1 -> l._2.map { s =>
                val prevLevel = if (userStats.levels.exists(l => l._1 == s._2.prevLevel)) {
                  Some(userStats.levels(s._2.prevLevel))
                } else None

                prevLevel match {
                  case Some(level) =>
                    val activate = level.filter(st => st._2.nextStep == "None").map(st => st._2.stars > 0).head
                    s._1 -> s._2.copy(active = activate)
                  case None => s
                }
              }
            }
        }
      )
    }

    def editStats(baseStats: Stats, userStats: Option[Stats]): Stats = userStats match {
      case Some(stats) => makeStatsSame(baseStats, stats)
      case None => baseStats
    }

    def apply(playerToken: PlayerToken, levels: Map[String, RawLevelData]): MakeStats = {
      new MakeStats(
        playerToken.copy(
          stats = Some(editStats(makeStats(levels), playerToken.stats))
        )
      )
    }
  }

  case class MakeStats(playerToken: PlayerToken)

  case class UpdatePlayerToken(playerToken: PlayerToken)

  case class EditLambdas(reqBody: JsValue)

  case class PreparedPlayerToken(playerToken: PlayerToken)

  object PrepareLambdas {
    implicit val prepareLambdasReads: Reads[PrepareLambdas] = (
      (JsPath \ "tokenId").read[String] and
      (JsPath \ "funcToken").read[FuncToken] and
      (JsPath \ "override").readNullable[Boolean]
    )(PrepareLambdas.apply _)
  }
  case class PrepareLambdas(tokenId: String, funcToken: FuncToken, `override`: Option[Boolean])

  case class PreparedLambdasToken(lambdas: Lambdas)

  def indexFunctions(funcs: List[FuncToken]): List[FuncToken] =
    funcs.zipWithIndex
      .map(ft => ft._1.copy(index = Option(ft._2)))

  def props(system: ActorSystem,
            reactiveMongoApi: ReactiveMongoApi,
            polyfillActor: ActorRef,
            logger: MathBotLogger,
            environment: Environment) =
    Props(new PlayerActor()(system, reactiveMongoApi, polyfillActor, logger, environment))
}

class PlayerActor()(system: ActorSystem,
                    val reactiveMongoApi: ReactiveMongoApi,
                    val polyfillActor: ActorRef,
                    logger: MathBotLogger,
                    environment: Environment)
    extends Actor
    with PlayerTokenModel {
  import PlayerActor._
  import context.dispatcher
  val levelGenerator: LevelGenerator = new LevelGenerator(environment)

  implicit val timeout: Timeout = 5000.minutes

  private val className = "PlayerActor"

  override def receive: Receive = {
    case addToken: AddToken =>
      Future { addToken.body.validate[PlayerToken].asOpt }
        .map {
          case Some(playerToken) => GetPlayerToken(playerToken)
          case None =>
            ActorFailed(
              s"Request body is invalid. ${addToken.body} expected ${Json.toJson(PlayerToken(token_id = "some|tokenId"))}"
            )
        }
        .pipeTo(self)(sender)
    case getPlayerToken: GetPlayerToken =>
      createOrGet(getPlayerToken.playerToken)
        .map { GatherLevel.apply }
        .pipeTo(self)(sender)
    case gatherLevel: GatherLevel =>
      val allLevels = levelGenerator.getAllLevels
      Future { (gatherLevel.playerToken, allLevels) }
        .map {
          case (playerToken, levels) if levels.nonEmpty => MakeStats(playerToken, levels)
          case _ => ActorFailed("No levels found.")
        }
        .pipeTo(self)(sender)
    case updateStats: MakeStats =>
      Future { updateStats.playerToken }
        .map { ApplyPolyfills.apply }
        .pipeTo(self)(sender)
    case applyPolyfills: ApplyPolyfills =>
      (polyfillActor ? applyPolyfills)
        .map {
          case updatePlayerToken: UpdatePlayerToken => updatePlayerToken
          case _ => ActorFailed("Failed to execute polyfill.")
        }
        .pipeTo(self)(sender)
    case updatePlayerToken: UpdatePlayerToken =>
      updateToken(updatePlayerToken.playerToken)
        .map { PreparedPlayerToken.apply }
        .pipeTo(self)(sender)
    case EditLambdas(jsValue) =>
      jsValue.validate[PrepareLambdas].asOpt match {
        case Some(prepareLambdas) =>
          (getToken(prepareLambdas.tokenId) map {
            case Some(playerToken) =>
              UpdateFunc(prepareLambdas.funcToken, playerToken, prepareLambdas.`override`.getOrElse(false))
            case None => ActorFailed("Unable to find player token")
          }).pipeTo(self)(sender)
        case None =>
          Future {}
            .map { _ =>
              ActorFailed("Invalid JSON")
            }
            .pipeTo(self)(sender)
      }
    case UpdateFunc(funcToken, playerToken, overrideBool) =>
      for {
        stats <- playerToken.stats
        lambdas <- playerToken.lambdas
        rsd <- levelGenerator.getRawStepData(stats.level, stats.step)
        rawStepData = rsd.copy(stagedQty = makeQtyUnlimited(rsd.stagedQty),
                               activeQty = makeQtyUnlimited(rsd.activeQty),
                               mainMax = makeQtyUnlimited(rsd.mainMax))
      } yield {
        val funcType = funcToken.`type`.getOrElse("Nothing")
        val mainFunc = lambdas.main.func.getOrElse(List.empty[FuncToken])

        val updatedPlayerToken =
          if (funcType == "function" || (funcType == "main-function" && mainFunc.lengthCompare(rawStepData.mainMax) < 0) || overrideBool) {
            val updatedLambdas = if (funcType == "function") {
              lambdas.copy(
                activeFuncs = indexFunctions(
                  lambdas.activeFuncs.map(f => if (f.created_id == funcToken.created_id) funcToken else f)
                )
              )
            } else {
              lambdas.copy(
                main = funcToken.copy(func = Some(indexFunctions(funcToken.func.getOrElse(List.empty[FuncToken]))))
              )
            }
            for {
              updatedPlayerToken <- updateToken(playerToken.copy(lambdas = Some(updatedLambdas)))
            } yield updatedPlayerToken
          } else {
            Future { playerToken }
          }

        updatedPlayerToken
          .map { pToken =>
            PreparedLambdasToken(
              PreparedStepData
                .prepareLambdas(pToken, rawStepData)
                .copy(activeFuncs = pToken.lambdas.get.activeFuncs)
            )
          }
          .pipeTo(self)(sender)
      }
    case ActivateFunc(tokenId, stagedIndex, activeIndex) =>
      getToken(tokenId)
        .map {
          case Some(playerToken) => MoveFunc(playerToken, stagedIndex, activeIndex)
          case None => ActorFailed("Unable to update lambdas.")
        }
        .pipeTo(self)(sender)
    case MoveFunc(playerToken, stagedIndex, activeIndex) =>
      for {
        stats <- playerToken.stats
        rsd <- levelGenerator.getRawStepData(stats.level, stats.step)
        lambdas <- playerToken.lambdas
        rawStepData = rsd.copy(stagedQty = makeQtyUnlimited(rsd.stagedQty),
                               activeQty = makeQtyUnlimited(rsd.activeQty),
                               mainMax = makeQtyUnlimited(rsd.mainMax))
      } yield
        if (rawStepData.activeEnabled && lambdas.activeFuncs.lengthCompare(
              makeQtyUnlimited(rawStepData.activeQty)
            ) <= 0) {
          for {
            lambdas <- playerToken.lambdas
            funcToMove <- lambdas.stagedFuncs.lift(stagedIndex.toInt)
            updatedStagedFuncs = lambdas.stagedFuncs
              .filterNot(_.index.contains(stagedIndex.toInt))

            updatedActiveFuncs = lambdas.activeFuncs
              .take(activeIndex.toInt) ++ List(funcToMove) ++ lambdas.activeFuncs
              .drop(activeIndex.toInt)
          } for {
            updatedToken <- updateToken(
              playerToken.copy(
                lambdas = Some(
                  lambdas.copy(stagedFuncs = indexFunctions(updatedStagedFuncs),
                               activeFuncs = indexFunctions(updatedActiveFuncs))
                )
              )
            )
          } yield
            Future { updatedToken }
              .map { pToken =>
                PreparedLambdasToken(PreparedStepData.prepareLambdas(pToken, rawStepData))
              }
              .pipeTo(self)(sender)
        } else {
          Future { PreparedStepData.prepareLambdas(playerToken, rawStepData) }
            .map { PreparedLambdasToken.apply }
            .pipeTo(self)(sender)
        }

    case updatedLambdasToken: PreparedLambdasToken =>
      logger.LogDebug(className, "PreparedLambdasToken generated.")
      sender ! Left(updatedLambdasToken)
    case preparedToken: PreparedPlayerToken =>
      logger.LogDebug(className, "PreparedPlayerToken generated.")
      sender ! Left(ResponsePlayerToken(preparedToken.playerToken))
    case actorFailed: ActorFailed =>
      logger.LogFailure(className, actorFailed.msg)
      sender ! Right(actorFailed)
    case _ =>
      logger.LogFailure(className, "Not sure what happened.")
      sender ! Right(ActorFailed("Not sure what happened. PlayerActor"))
  }
}
