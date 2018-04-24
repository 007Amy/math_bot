package actors.messages

import model.models.{PlayerToken, Stats}
import play.api.libs.json.Json

object ResponsePlayerToken {
  def apply(playerToken: PlayerToken): ResponsePlayerToken = {
    val lambdas = playerToken.lambdas.get
    new ResponsePlayerToken(
      playerToken.token_id,
      ResponseLambdas(lambdas),
      playerToken.stats,
      playerToken.randomImages
    )
  }

  implicit val jsonFormat = Json.format[ResponsePlayerToken]
}

case class ResponsePlayerToken(
    token_id: String,
    lambdas: ResponseLambdas,
    stats: Option[Stats],
    randomImages: Option[List[String]]
)
