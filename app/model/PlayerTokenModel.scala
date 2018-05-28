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
}
