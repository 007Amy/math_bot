package actors

import actors.PlayerActor.UpdatePlayerToken
import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.pipe
import loggers.MathBotLogger
import model.DefaultCommands
import model.models._
import play.api.Environment

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PolyfillActor {
  final case class ApplyPolyfills(playerToken: PlayerToken)

  final case class AddDefaultFuncsField(playerToken: PlayerToken)

  final case class AddNamesToCommands(playerToken: PlayerToken)

  final case class DedupFunctions(playerToken: PlayerToken)

  final case class PolyfillsApplied(playerToken: PlayerToken)

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
          .map { PolyfillsApplied.apply }
          .pipeTo(self)(sender)

    case DedupFunctions(playerToken) => // ensures user only has one instance of pre-built active or assigned staged
      ???

    case PolyfillsApplied(playerToken) =>
      sender ! UpdatePlayerToken(playerToken)
  }
}
