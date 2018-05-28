package model

import model.models.{FuncToken, Lambdas, PlayerToken}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import play.modules.reactivemongo._

import scala.concurrent._

// BSON-JSON conversions/collection
import play.api.libs.json._
import reactivemongo.play.json._
import reactivemongo.play.json.collection._

trait PlayerTokenModel extends MongoController with ReactiveMongoComponents with Controller {

  private def collection: Future[JSONCollection] = database.map(
    _.collection[JSONCollection]("tokens")
  )

  def getTokenCount: Future[Int] =
    for {
      db <- collection
      count <- db.count()
    } yield count

  def getToken(tokenId: String): Future[Option[PlayerToken]] =
    for {
      db <- collection
      potentialToken <- db.find(Json.obj("token_id" -> tokenId)).one[PlayerToken]
    } yield potentialToken

  def createOrGet(vToken: PlayerToken): Future[PlayerToken] =
    getToken(vToken.token_id).flatMap {
      case None =>
        val token =
          vToken.copy(stats = None, lambdas = Some(Lambdas()), randomImages = Some(List.empty[String]))
        insert(token).map(t => t)
      case pt if pt.head.isInstanceOf[PlayerToken] =>
        Future.successful(pt.head)
    }

  def insert(valToken: PlayerToken): Future[PlayerToken] = {
    val add = collection flatMap {
      _.insert(valToken)
    }
    add map { _ =>
      valToken
    }
  }

  def delete(tokenId: String) = {
    for {
      db <- collection
      removeToken <- db.remove(Json.obj("token_id" -> tokenId))
    } yield removeToken
  }

  def updateToken(token: PlayerToken): Future[PlayerToken] = {
    collection flatMap { v =>
      v.update(Json.obj("token_id" -> token.token_id), token)
    } map { _ =>
      token
    }
  }

  def updateFunc(tokenId: String, func: FuncToken): Future[Option[PlayerToken]] = {
    getToken(tokenId).flatMap {
      case Some(playerToken) =>
        val lambdas = playerToken.lambdas.get
        val updatedLambdas = if (func.`type`.contains("function")) {
          lambdas.copy(activeFuncs = lambdas.activeFuncs.map(f => if (f.created_id == func.created_id) func else f))
        } else {
          lambdas.copy(main = func)
        }
        for {
          updatedPlayerToken <- updateToken(playerToken.copy(lambdas = Some(updatedLambdas)))
        } yield Some(updatedPlayerToken)
      case None =>
        Future {
          None
        }
    }
  }

  def activateFunc(tokenId: String, stagedIndex: String, activeIndex: String): Future[Option[PlayerToken]] = {
    getToken(tokenId).flatMap {
      case Some(playerToken) =>
        val lambdas = playerToken.lambdas.get
        val funcToMove = lambdas.stagedFuncs.lift(stagedIndex.toInt).get
        val updatedStagedFuncs = lambdas.stagedFuncs
          .filterNot(_.index.contains(stagedIndex.toInt))
          .zipWithIndex
          .map(ft => ft._1.copy(index = Option(ft._2)))

        val updatedActiveFuncs = lambdas.activeFuncs
          .take(activeIndex.toInt) ++ List(funcToMove) ++ lambdas.activeFuncs
          .drop(activeIndex.toInt)
          .zipWithIndex
          .map(ft => ft._1.copy(index = Option(ft._2)))

        for {
          updatedToken <- updateToken(
            playerToken.copy(
              lambdas = Some(lambdas.copy(stagedFuncs = updatedStagedFuncs, activeFuncs = updatedActiveFuncs))
            )
          )
        } yield Some(updatedToken)
      case None => Future { None }
    }
  }
}
