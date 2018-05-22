package actors

import java.io.File

import actors.messages._
import akka.actor.{Actor, Props}
import akka.pattern.pipe
import loggers.MathBotLogger
import model.{models, DefaultCommands, PlayerTokenModel}
import model.models._
import play.api.Environment
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import messages.PreparedStepData._

import java.security.MessageDigest

import scala.concurrent.Future
import scala.io.Source

class LevelGenerator(environment: Environment) {

  private val dirName = "assets"

  def getListOfLevels: List[String] = {
    environment.getExistingFile(dirName).map { dir =>
      dir.listFiles.filter(_.isFile).map(_.getAbsolutePath).toList
    } orElse {
      environment.getExistingFile(s"conf/$dirName").map { dir =>
        dir.listFiles.filter(_.isFile).map(_.getAbsolutePath).toList
      }
    }
  }.getOrElse(List.empty[String])

  def getAllLevels: Map[String, RawLevelData] = {
    val levels = getListOfLevels
      .map { level =>
        getJsonFromFile(level) match {
          case Some(rawLevelData) =>
            if (rawLevelData.show) {
              (rawLevelData.level, Some(rawLevelData))
            } else ("nothing", None)
          case None => ("nothing", None)
        }
      }
      .toMap
      .filterNot(rld => rld._2.isEmpty)
      .map(rld => (rld._1, rld._2.get))

    levels.map { rld =>
      levels.get(rld._2.nextLevel) match {
        case Some(_) => rld
        case None => rld._1 -> rld._2.copy(nextLevel = "None")
      }
    }
  }

  def getJsonFromFile(filePath: String): Option[RawLevelData] =
    Json.parse(Source.fromFile(filePath).getLines().mkString("")).validate[RawLevelData].asOpt

  def getRawStepData(level: String, step: String): Option[RawStepData] =
    getAllLevels.get(level) match {
      case Some(levelData) => levelData.steps.get(step)
      case None => None
    }
}

object LevelGenerationActor {
  case class GetStepControl(level: String, step: String)

  case class GetPlayerToken(tokenId: String, level: String, step: String)

  case class PlayerTokenReceived(playerToken: PlayerToken, level: String, step: String)

  case class UpdateDb(playerToken: PlayerToken, rawStepData: RawStepData)

  case class PrepareStepData(playerToken: PlayerToken, rawStepData: RawStepData)

  case class ActorFailed(msg: String = "Json did not match any type.")

  case class GetGridMap(playerToken: Option[PlayerToken])

  case class StepData(rawStepData: Option[RawStepData], preparedStepData: Option[PreparedStepData])

  case class ProcessStepData(playerToken: PlayerToken)

  case class GetLevel(level: String, tokenId: Option[String])

  case class ResetStagedFunctions(playerToken: PlayerToken, rawStepData: RawStepData)

  case class GetStep(level: String, step: String, tokenId: Option[String] = None)

  def makeQtyUnlimited(qty: Int): Int = if (qty < 0) 10000 else qty

  def createdIdGen(name: String): String = {
    MessageDigest.getInstance("MD5").digest(name.getBytes).mkString("")
  }

  def props(reactiveMongoApi: ReactiveMongoApi, logger: MathBotLogger, environment: Environment) =
    Props(new LevelGenerationActor()(reactiveMongoApi, logger, environment))
}

class LevelGenerationActor()(val reactiveMongoApi: ReactiveMongoApi, logger: MathBotLogger, environment: Environment)
    extends Actor
    with PlayerTokenModel {
  import LevelGenerationActor._
  import context.dispatcher

  val levelGenerator: LevelGenerator = new LevelGenerator(environment)

  private val className = "LevelGenerationActor"

  override def receive: Receive = {
    case GetGridMap(playerToken) =>
      playerToken match {
        case Some(token) =>
          token.stats match {
            case Some(stats) =>
              levelGenerator.getRawStepData(stats.level, stats.step) match {
                case Some(rawStepData) =>
                  val preparedStepData =
                    PreparedStepData(
                      token,
                      rawStepData.copy(activeQty = makeQtyUnlimited(rawStepData.activeQty),
                                       stagedQty = makeQtyUnlimited(rawStepData.stagedQty),
                                       mainMax = makeQtyUnlimited(rawStepData.mainMax))
                    )

                  Future { preparedStepData }
                    .map { psd =>
                      logger.LogDebug(className, "Successfully created GridMap")
                      GridMap(
                        gMap = psd.gridMap,
                        robotOrientation = rawStepData.robotOrientation.toString,
                        success = psd.stepControl.success,
                        description = psd.description,
                        problem = Problem(psd.problem.encryptedProblem)
                      )
                    }
                    .pipeTo(self)(sender)

                case None => sender ! Left(ActorFailed(s"No level: ${stats.level} or step: ${stats.step}"))
              }
            case None => sender ! Left(ActorFailed(s"No stats with token_id ${token.token_id}"))
          }
        case None => sender ! Left(ActorFailed("Hey that's not a PlayerToken!"))
      }
    case GetLevel(level, _) =>
      Future { levelGenerator.getJsonFromFile(s"app/assets/$level.json") }
        .map {
          case Some(rawLevelData) => rawLevelData
          case None => ActorFailed(s"No $level found in assets.")
        }
        .pipeTo(self)(sender)
    case GetStep(level, step, id) =>
      id match {
        case Some(tokenId) =>
          Future { tokenId }
            .map { GetPlayerToken(_, level, step) }
            .pipeTo(self)(sender)
        case None =>
          Future { levelGenerator.getRawStepData(level, step) }
            .map {
              case Some(rawStepData) => rawStepData
              case None => ActorFailed(s"No level $level or step $step found.")
            }
            .pipeTo(self)(sender)
      }
    case GetPlayerToken(tokenId, level, step) =>
      getToken(tokenId)
        .map {
          case Some(token) => PlayerTokenReceived(token, level, step)
          case None => ActorFailed(s"No token found with token_id $tokenId")
        }
        .pipeTo(self)(sender)
    case PlayerTokenReceived(playerToken, level, step) =>
      Future { levelGenerator.getRawStepData(level, step) }
        .map {
          case Some(rawStepData) =>
            ResetStagedFunctions(
              playerToken,
              rawStepData.copy(stagedQty = makeQtyUnlimited(rawStepData.stagedQty),
                               activeQty = makeQtyUnlimited(rawStepData.activeQty),
                               mainMax = makeQtyUnlimited(rawStepData.mainMax))
            )
          case None => ActorFailed(s"No level $level or step $step found.")
        }
        .pipeTo(self)(sender)
    case ResetStagedFunctions(playerToken, rawStepData) =>
      // Resets staged to zero before UpdateDb
      // Moves all staged functions back to defaultFuncs
      Future {
        playerToken.lambdas match {
          case Some(lambdas) =>
            val updatedDefault = lambdas.defaultFuncs.get ::: lambdas.stagedFuncs
            val defaultIds = DefaultCommands.funcs.map(_.created_id)
            val filteredDefault = updatedDefault.filter(d => defaultIds.contains(d.created_id))

            val r = lambdas.copy(
              stagedFuncs = List.empty[FuncToken],
              defaultFuncs = Some(filteredDefault)
            )
            UpdateDb(playerToken.copy(lambdas = Some(r)), rawStepData)
          case None =>
        }
      }.pipeTo(self)(sender)
    case UpdateDb(playerToken, rawStepData) =>
      playerToken.stats match {
        /*
         * If user has played this level do not update with generated function data
         * Just reset main func if step requires it
         * */
        case Some(stats) if stats.levels(rawStepData.level)(rawStepData.step).timesPlayed > 0 =>
          for {
            lambdas <- playerToken.lambdas
            mainFunc <- lambdas.main.func
            newMainFunc = if (rawStepData.clearMain) List.empty[FuncToken] else mainFunc
            newMain = lambdas.main.copy(func = Some(newMainFunc))
            updatedLambdas = lambdas.copy(main = newMain)
          } yield {
            Future { playerToken.copy(lambdas = Some(updatedLambdas)) }
              .map { PreparedStepData(_, rawStepData) }
              .pipeTo(self)(sender)
          }
        /*
         * Else add generated function data
         * */
        case _ =>
          for {
            lambdas <- playerToken.lambdas
            activeFuncs = lambdas.activeFuncs
          } yield {
            // Create List[FuncToken] of assigned staged
            val assignedStaged =
              rawStepData.assignedStaged.toList.map { s =>
                val name = s._1
                val image = s._2
                FuncToken(
                  created_id = createdIdGen(name),
                  func = Option(List.empty[FuncToken]),
                  set = Some(false),
                  name = Some(parseCamelCase(name)),
                  image = Some(image),
                  index = Some(playerToken.lambdas.get.stagedFuncs.length),
                  `type` = Some("function"),
                  commandId = Some("function")
                )
              }
            // Create List[FuncToken] of pre built active functions
            val preBuiltActive = rawStepData.preBuiltActive.toList
              .map { p =>
                val name = p._1
                val func = p._2.map { c =>
                  playerToken.lambdas.get.cmds.find(v => v.commandId.contains(c)).get
                }
                models.FuncToken(
                  created_id = createdIdGen(name),
                  func = Some(func),
                  name = Some(parseCamelCase(name)),
                  image = Some("rocket"),
                  index = Some(playerToken.lambdas.get.activeFuncs.length),
                  `type` = Some("function"),
                  commandId = Some("function")
                )
              }

            // Move new staged function between defualt and staged functions
            val newStagedAndDefault: Map[String, List[FuncToken]] = {
              val stagedFuncs = lambdas.stagedFuncs
              val defaultFuncs = lambdas.defaultFuncs.getOrElse(DefaultCommands.funcs)
              val qty = rawStepData.stagedQty

              val newStaged = assignedStaged ++ defaultFuncs.take(qty)
              val newDefault = defaultFuncs.filterNot(d => newStaged.exists(_.created_id == d.created_id))

              Map("newStaged" -> newStaged, "newDefault" -> newDefault)
            }

            //  Commands
            val cmds = lambdas.cmds

            // Copy lambdas and update values
            val l = lambdas.copy(
              cmds = cmds,
              stagedFuncs = newStagedAndDefault("newStaged"),
              defaultFuncs = Some(newStagedAndDefault("newDefault")),
              activeFuncs = preBuiltActive ::: preBuiltActive ::: activeFuncs.zipWithIndex
                .map(d => d._1.copy(index = Some(d._2))),
              main =
                if (rawStepData.clearMain) playerToken.lambdas.get.main.copy(func = Some(List.empty[FuncToken]))
                else playerToken.lambdas.get.main
            )

            // Update db with new token
            updateToken(playerToken.copy(lambdas = Some(l)))
              .map(
                PreparedStepData(_, rawStepData)
              )
              .pipeTo(self)(sender)
          }
      }

    case gridMap: GridMap =>
      sender ! gridMap
    case preparedStepData: PreparedStepData =>
      logger.LogDebug(className, "PreparedStepData generated.")
      sender ! Left(preparedStepData)
    case rawLevelData: RawLevelData =>
      logger.LogDebug(className, "RawLevelData generated.")
      sender ! Left(rawLevelData)
    case rawStepData: RawStepData =>
      logger.LogDebug(className, "RawStepData generated.")
      sender ! rawStepData
    case actorFailed: ActorFailed =>
      logger.LogFailure(className, s"ActorFailed generated. msg:${actorFailed.msg}")
      sender ! Right(actorFailed)
    case _ =>
      logger.LogFailure(className, "Not sure what happened.")
      sender ! Right(ActorFailed("Not sure what happened. LevelGenerationActor"))
  }
}
