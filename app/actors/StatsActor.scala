package actors

import actors.LevelGenerationActor.ActorFailed
import actors.PlayerActor.UpdatePlayerToken
import akka.actor.{Actor, ActorSystem, PoisonPill, Props}
import akka.pattern.pipe
import com.google.inject.Inject
import com.google.inject.name.Named
import loggers.MathBotLogger
import model.models.{Stats, StepToken}
import play.modules.reactivemongo.ReactiveMongoApi

object StatsActor {

  case class ChangeLevel(tokenId: String, level: String, step: String)

  case class Unlock(tokenId: String)

  case class Reset(tokenId: String)

  case class StatsDoneUpdating(tokenId: String, stats: Stats)

  case class GetStats(tokenId: String)

  case class UpdateStats(success: Boolean, tokenId: String)

  def updateStats(stats: Stats) = {
    val currentLevelName: String = stats.level
    val currentStepName: String = stats.step

    val currentLevel: Map[String, StepToken] = stats.levels(currentLevelName)
    val currentStep: StepToken = currentLevel(currentStepName)

    val updatedCurrentStep: StepToken = currentStep.copy(stars = 5, timesPlayed = currentStep.timesPlayed + 1)

    if (updatedCurrentStep.nextLevel == "None" && updatedCurrentStep.nextStep == "None") {

      stats.copy(
        levels = stats.levels
          .map(
            l =>
              if (l._1 == currentLevelName)
                l._1 -> currentLevel
                  .map(st => if (st._1 == updatedCurrentStep.name) st._1 -> updatedCurrentStep else st)
              else l
          )
      )

    } else {

      val updatedNextStep: StepToken =
        if (currentStep.nextStep == "None" && currentStep.nextLevel != "None") {
          stats.levels(currentStep.nextLevel).find(_._2.prevStep == "None").get._2.copy(active = true)
        } else {
          currentLevel(currentStep.nextStep).copy(active = true)
        }

      val updatedCurrentLevel: Map[String, StepToken] = {
        if (currentStep.nextStep == "None") {
          currentLevel
            .map(st => if (st._1 == updatedCurrentStep.name) st._1 -> updatedCurrentStep else st)
        } else {
          currentLevel
            .map(st => if (st._1 == updatedCurrentStep.name) st._1 -> updatedCurrentStep else st)
            .map(st => if (st._1 == updatedNextStep.name) st._1 -> updatedNextStep else st)
        }
      }

      val updatedNextLevel: Option[Map[String, StepToken]] =
        if (currentStep.nextStep == "None") {
          Some(
            stats
              .levels(currentStep.nextLevel)
              .map(st => if (st._1 == updatedNextStep.name) st._1 -> updatedNextStep else st)
          )
        } else None

      val nextLevelName =
        if (currentStep.nextStep == "None") currentStep.nextLevel
        else stats.level

      val nextStepName = updatedNextStep.name

      stats.copy(
        level = nextLevelName,
        step = nextStepName,
        levels = stats.levels
          .map(l => if (l._1 == currentLevelName) l._1 -> updatedCurrentLevel else l)
          .map(
            l =>
              updatedNextLevel match {
                case Some(level) =>
                  if (l._1 == nextLevelName) l._1 -> level
                  else l
                case None => l
            }
          )
      )
    }
  }

  def props(system: ActorSystem, reactiveMongoApi: ReactiveMongoApi, logger: MathBotLogger) =
    Props(new StatsActor(system, reactiveMongoApi, logger))
}

class StatsActor @Inject()(val system: ActorSystem, val reactiveMongoApi: ReactiveMongoApi, logger: MathBotLogger)
    extends Actor
    with model.PlayerTokenModel {

  import StatsActor._
  import context.dispatcher

  private val className = "StatsActor"

  override def receive: Receive = {
    case GetStats(tokenId) =>
      getToken(tokenId)
        .map {
          case Some(token) =>
            token.stats match {
              case Some(stats) => StatsDoneUpdating(tokenId, stats)
              case None => ActorFailed("No stats with this player token.")
            }
          case None => ActorFailed(s"No token found with token_id $tokenId.")
        }
        .pipeTo(self)(sender)
    case UpdateStats(success, tokenId) =>
      if (success) {
        getToken(tokenId)
          .map {
            case Some(token) =>
              token.stats match {
                case Some(stats) =>
                  val updatedStats = updateStats(stats)
                  UpdatePlayerToken(token.copy(stats = Some(updatedStats)))
                case None => ActorFailed("No stats with this player token.")
              }
            case None => ActorFailed(s"No token found with token_id $tokenId.")
          }
          .pipeTo(self)(sender)
      } else sender ! Right(ActorFailed("Nothing to update."))
    case ChangeLevel(tokenId, level, step) =>
      getToken(tokenId)
        .map {
          case Some(token) =>
            UpdatePlayerToken(token.copy(stats = Some(token.stats.get.copy(level, step))))
          case None => ActorFailed(s"No token found with token_id $tokenId.")
        }
        .pipeTo(self)(sender)

    case updatePlayerToken: UpdatePlayerToken =>
      updateToken(updatePlayerToken.playerToken)
        .map { pt =>
          pt.stats match {
            case Some(stats) => StatsDoneUpdating(updatePlayerToken.playerToken.token_id, stats)
            case None => ActorFailed("No stats with this player token.")
          }
        }
        .pipeTo(self)(sender)
    case Unlock(tokenId) =>
      getToken(tokenId)
        .map {
          case Some(token) =>
            token.stats match {
              case Some(stats) =>
                val unlockedLevels = stats.levels
                  .map { level =>
                    (level._1, level._2.map { step =>
                      (step._1, step._2.copy(active = true))
                    })
                  }
                UpdatePlayerToken(token.copy(stats = Some(stats.copy(levels = unlockedLevels))))
              case None => ActorFailed("No stats with this player token.")
            }

          case None => ActorFailed(s"No token found with token_id $tokenId.")
        }
        .pipeTo(self)(sender)
    case Reset(tokenId) =>
      getToken(tokenId)
        .map {
          case Some(token) =>
            token.stats match {
              case Some(stats) =>
                val resetLevels = stats.levels
                  .map { level =>
                    (level._1, level._2.map { step =>
                      (step._1, step._2.copy(active = step._2.prevStep == "None" && step._2.prevLevel == "None"))
                    })
                  }
                UpdatePlayerToken(token.copy(stats = Some(stats.copy(levels = resetLevels))))
              case None => ActorFailed("No stats with this player token.")
            }
          case None => ActorFailed(s"No token found with token_id $tokenId")
        }
        .pipeTo(self)(sender)
    case doneUpdating: StatsDoneUpdating =>
      logger.LogInfo(className, s"Stats updated successfully. token_id: ${doneUpdating.tokenId}")
      sender ! Left(doneUpdating)
    case actorFailed: ActorFailed =>
      logger.LogFailure(className, actorFailed.msg)
      sender ! Right(actorFailed)
    case _ =>
      logger.LogFailure(className, "Not sure what happened.")
      sender ! Right(ActorFailed("Not sure what happened. StatsActor"))
  }
}
