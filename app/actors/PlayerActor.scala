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

import scala.concurrent.Future

object PlayerActor {

  case class AddToken(body: JsValue)

  case class GetPlayerToken(playerToken: PlayerToken)

  case class GatherLevel(playerToken: PlayerToken)

  case class UpdateFunc(funcToken: FuncToken, playerToken: Option[PlayerToken], `override`: Boolean)

  case class ActivateFunc(tokenId: String, stagedIndex: String, activeIndex: String)

  case class MoveFunc(playerToken: PlayerToken, stagedIndex: String, activeIndex: String)

  case class AddDefaultFuncsField(playerToken: PlayerToken)

  case class AddNamesToCommands(playerToken: PlayerToken)

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

      val updated = userStats.copy(
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
              l._1 -> l._2.map(
                s =>
                  if (s._2.prevLevel == "None" && s._2.prevStep == "None") s._1 -> s._2.copy(active = true)
                  else s
              )
            }
        }
      )

      val p = 0

      updated
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

  def props(system: ActorSystem, reactiveMongoApi: ReactiveMongoApi, logger: MathBotLogger, environment: Environment) =
    Props(new PlayerActor()(system, reactiveMongoApi, logger, environment))
}

class PlayerActor()(system: ActorSystem,
                    val reactiveMongoApi: ReactiveMongoApi,
                    logger: MathBotLogger,
                    environment: Environment)
    extends Actor
    with PlayerTokenModel {
  import PlayerActor._
  import context.dispatcher

  val levelGenerator: LevelGenerator = new LevelGenerator(environment)

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
        .map { UpdatePlayerToken.apply }
        .pipeTo(self)(sender)
    case updatePlayerToken: UpdatePlayerToken =>
      updateToken(updatePlayerToken.playerToken)
        .map { pt =>
          pt.lambdas.get.defaultFuncs match {
            case Some(_) => AddNamesToCommands(pt)
            case None => AddDefaultFuncsField(pt)
          }
        }
        .pipeTo(self)(sender)
    case AddDefaultFuncsField(playerToken) => // Polyfill to ensure field added to existing users
      playerToken.lambdas match {
        case Some(lambdas) =>
          updateToken(
            playerToken
              .copy(
                lambdas = Some(
                  lambdas.copy(stagedFuncs = List.empty[FuncToken], defaultFuncs = Some(DefaultCommands.funcs))
                )
              )
          ).map { AddNamesToCommands.apply }
            .pipeTo(self)(sender)
          logger.LogInfo(className, "Adding default tokens")
        case None =>
          Future {}
            .map { _ =>
              ActorFailed("Unable to locate lambdas at AddDefaultFuncsField")
            }
            .pipeTo(self)(sender)
      }
    case AddNamesToCommands(playerToken) => // Polyfill to ensure commands include names for existing users
      playerToken.lambdas match {
        case Some(lambdas) =>
          updateToken(
            playerToken.copy(lambdas = Some(lambdas.copy(cmds = DefaultCommands.cmds)))
          ).map { PreparedPlayerToken.apply }
            .pipeTo(self)(sender)
        case None =>
          Future {}
            .map { _ =>
              ActorFailed("Unable to locate lambdas at AddNamesToCommands")
            }
            .pipeTo(self)(sender)
      }
    case EditLambdas(jsValue) =>
      jsValue.validate[PrepareLambdas].asOpt match {
        case Some(prepareLambdas) =>
          getToken(prepareLambdas.tokenId)
            .map { playerToken =>
              UpdateFunc(prepareLambdas.funcToken, playerToken, prepareLambdas.`override`.getOrElse(false))
            }
            .pipeTo(self)(sender)
        case None =>
          Future {}
            .map { _ =>
              ActorFailed(s"Data structure not appropriate, $jsValue")
            }
            .pipeTo(self)(sender)
      }
    case UpdateFunc(func, token, overrideBool) =>
      token match {
        case Some(playerToken) =>
          playerToken.stats match {
            case Some(stats) =>
              playerToken.lambdas match {
                case Some(lambdas) =>
                  levelGenerator.getRawStepData(stats.level, stats.step) match {
                    case Some(rsd) =>
                      val rawStepData = rsd.copy(stagedQty = makeQtyUnlimited(rsd.stagedQty),
                                                 activeQty = makeQtyUnlimited(rsd.activeQty),
                                                 mainMax = makeQtyUnlimited(rsd.mainMax))
                      func.`type` match {
                        case Some("function") =>
                          updateFunc(playerToken.token_id, func)
                            .map {
                              case Some(pToken) =>
                                PreparedLambdasToken(
                                  PreparedStepData
                                    .prepareLambdas(pToken, rawStepData)
                                    .copy(activeFuncs = pToken.lambdas.get.activeFuncs)
                                )
                              case None => ActorFailed("Something went wrong while updating func.")
                            }
                            .pipeTo(self)(sender)
                        case Some("main-function") =>
                          lambdas.main.func match {
                            case Some(mainFunc) =>
                              if (mainFunc.lengthCompare(rawStepData.mainMax) < 0 || overrideBool) {
                                updateFunc(playerToken.token_id, func)
                                  .map {
                                    case Some(pToken) =>
                                      PreparedLambdasToken(
                                        PreparedStepData
                                          .prepareLambdas(pToken, rawStepData)
                                          .copy(activeFuncs = pToken.lambdas.get.activeFuncs)
                                      )
                                    case None => ActorFailed("Something went wrong while updating func.")
                                  }
                                  .pipeTo(self)(sender)
                              } else {
                                Future {
                                  playerToken
                                }.map { token =>
                                    PreparedLambdasToken(
                                      PreparedStepData
                                        .prepareLambdas(token, rawStepData)
                                        .copy(activeFuncs = token.lambdas.get.activeFuncs)
                                    )
                                  }
                                  .pipeTo(self)(sender)
                              }
                            case None => sender ! ActorFailed("No func in main.")
                          }
                        case _ => sender ! ActorFailed("Not a valid command type.")
                      }
                    case None => sender ! ActorFailed(s"No step found for ${stats.level}/${stats.step}")
                  }
                case None => sender ! ActorFailed(s"No stats with player token.")
              }
            case None => sender ! ActorFailed("Player token has no lambdas.")
          }
        case None => sender ! ActorFailed(s"Unable to find a player token.")
      }
    case ActivateFunc(tokenId, stagedIndex, activeIndex) =>
      getToken(tokenId)
        .map {
          case Some(playerToken) => MoveFunc(playerToken, stagedIndex, activeIndex)
          case None => ActorFailed("Unable to update lambdas.")
        }
        .pipeTo(self)(sender)
    case MoveFunc(playerToken, stagedIndex, activeIndex) =>
      playerToken.stats match {
        case Some(stats) =>
          levelGenerator.getRawStepData(stats.level, stats.step) match {
            case Some(rsd) =>
              val rawStepData =
                rsd.copy(stagedQty = makeQtyUnlimited(rsd.stagedQty),
                         activeQty = makeQtyUnlimited(rsd.activeQty),
                         mainMax = makeQtyUnlimited(rsd.mainMax))
              playerToken.lambdas match {
                case Some(lambdas) =>
                  if (rawStepData.activeEnabled && lambdas.activeFuncs.lengthCompare(
                        LevelGenerationActor.makeQtyUnlimited(rawStepData.activeQty)
                      ) <= 0) {
                    activateFunc(playerToken.token_id, stagedIndex, activeIndex)
                      .map {
                        case Some(token) =>
                          PreparedLambdasToken(
                            PreparedStepData.prepareLambdas(token, rawStepData)
                          )
                        case None => ActorFailed("Unable to update lambdas.")
                      }
                      .pipeTo(self)(sender)
                  } else {
                    Future { playerToken }
                      .map { token =>
                        PreparedLambdasToken(PreparedStepData.prepareLambdas(token, rawStepData))
                      }
                      .pipeTo(self)(sender)
                  }
                case None =>
                  Future {}
                    .map { _ =>
                      ActorFailed("Unable to move func.")
                    }
                    .pipeTo(self)(sender)
              }
            case None =>
              Future {}
                .map { _ =>
                  ActorFailed("Unable to move func.")
                }
                .pipeTo(self)(sender)
          }
        case None =>
          Future {}
            .map { _ =>
              ActorFailed("Unable to move func.")
            }
            .pipeTo(self)(sender)
      }

    case updatedLambdasToken: PreparedLambdasToken =>
      logger.LogInfo(className, "PreparedLambdasToken generated.")
      sender ! Left(updatedLambdasToken)
    case preparedToken: PreparedPlayerToken =>
      logger.LogInfo(className, "PreparedPlayerToken generated.")
      sender ! Left(ResponsePlayerToken(preparedToken.playerToken))
    case actorFailed: ActorFailed =>
      logger.LogFailure(className, actorFailed.msg)
      sender ! Right(actorFailed)
    case _ =>
      logger.LogFailure(className, "Not sure what happened.")
      sender ! Right(ActorFailed("Not sure what happened. PlayerActor"))
  }
}
