package actors

import actors.PlayerActor.UpdatePlayerToken
import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.pipe
import loggers.MathBotLogger
import model.DefaultCommands
import model.models._
import play.api.Environment

import actors.LevelGenerationActor.createdIdGen

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PolyfillActor {
  final case class ApplyPolyfills(playerToken: PlayerToken)

  final case class AddDefaultFuncsField(playerToken: PlayerToken)

  final case class AddNamesToCommands(playerToken: PlayerToken)

  final case class DedupFunctions(playerToken: PlayerToken)

  final case class PolyfillsApplied(playerToken: PlayerToken)

  def dedup(lambdas: Lambdas): Lambdas = {
    // Dedup active functions, replace created id
    def dedupActives(actives: List[FuncToken]): List[FuncToken] = actives match {
      case Nil => Nil
      case ft :: rest if rest.exists(_.image == ft.image) =>
        ft.copy(created_id = createdIdGen(ft.image.get)) :: dedupActives(rest.filterNot(_.image == ft.image))
      case ft :: rest => ft :: dedupActives(rest)
    }

    // Update created id for all instances of active functions
    def updateCreatedIds(actives: List[FuncToken]): List[FuncToken] = {
      actives.map { ft =>
        val func = ft.func.getOrElse(List.empty[FuncToken])
        val updatedFunc = func.map { t =>
          actives.find(_.image == t.image) match {
            case Some(activeToken) => t.copy(created_id = activeToken.created_id)
            case None => t
          }
        }
        ft.copy(func = Some(updatedFunc))
      }
    }

    // Store updated active functions
    val updatedActive = updateCreatedIds(dedupActives(lambdas.activeFuncs))

    // Replace created ids for any deduped found in main
    val updatedMain = lambdas.main.copy(func = Some(lambdas.main.func.getOrElse(List.empty[FuncToken]).map { ft =>
      updatedActive.find(_.image == ft.image) match {
        case Some(funcToken) => ft.copy(created_id = funcToken.created_id)
        case None => ft
      }
    }))

    lambdas.copy(activeFuncs = updatedActive, main = updatedMain)
  }

  def props(system: ActorSystem, logger: MathBotLogger, environment: Environment) =
    Props(new PolyfillActor()(system, logger, environment))
}

class PolyfillActor()(system: ActorSystem, logger: MathBotLogger, environment: Environment) extends Actor {
  import PolyfillActor._
  private val className = "PolyfillActor"

  override def receive: Receive = {
    case ApplyPolyfills(playerToken) =>
      Future { playerToken.lambdas.get.defaultFuncs }
        .map {
          case Some(_) => AddNamesToCommands(playerToken)
          case None => AddDefaultFuncsField(playerToken)
        }
        .pipeTo(self)(sender)
    case AddDefaultFuncsField(playerToken) => // Polyfill to ensure field added to existing users
      for {
        lambdas <- playerToken.lambdas
        updatedLambdas = lambdas.copy(stagedFuncs = List.empty[FuncToken], defaultFuncs = Some(DefaultCommands.funcs))
      } yield
        Future { playerToken.copy(lambdas = Some(updatedLambdas)) }
          .map { AddNamesToCommands.apply }
          .pipeTo(self)(sender)
    case AddNamesToCommands(playerToken) => // Polyfill to ensure commands include names for existing users
      for {
        lambdas <- playerToken.lambdas
        updatedLambdas = Some(lambdas.copy(cmds = DefaultCommands.cmds))
      } yield
        Future { playerToken.copy(lambdas = updatedLambdas) }
          .map { DedupFunctions.apply }
          .pipeTo(self)(sender)
    case DedupFunctions(playerToken) => // ensures user only has one instance of pre-built active or assigned staged?
      for {
        lambdas <- playerToken.lambdas
        deduped = dedup(lambdas)
      } yield
        Future { playerToken.copy(lambdas = Some(deduped)) }
          .map { PolyfillsApplied.apply }
          .pipeTo(self)(sender)

    case PolyfillsApplied(playerToken) =>
      sender ! UpdatePlayerToken(playerToken)
  }
}
