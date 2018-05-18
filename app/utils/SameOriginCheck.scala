package utils

import java.net.URI

import loggers.MathBotLogger

import scala.util.Properties._
import play.api.mvc.RequestHeader

trait SameOriginCheck {

  val logger = new MathBotLogger

  /**
   * Checks that the WebSocket comes from the same origin.  This is necessary to protect
   * against Cross-Site WebSocket Hijacking as WebSocket does not implement Same Origin Policy.
   *
   * See https://tools.ietf.org/html/rfc6455#section-1.3 and
   * http://blog.dewhurstsecurity.com/2013/08/30/security-testing-html5-websockets.html
   */
  def sameOriginCheck(implicit rh: RequestHeader): Boolean =
    // The Origin header is the domain the request originates from.
    // https://tools.ietf.org/html/rfc6454#section-7
    //    println("Checking the ORIGIN ")
    rh.headers.get("Origin") match {
      case Some(originValue) if originMatches(originValue) =>
        //        println(s"originCheck: originValue = $originValue")
        true

      case Some(badOrigin) =>
        //        println(s"originCheck: rejecting request because Origin header value ${badOrigin} is not in the same origin")
        false

      case None =>
        //        println("originCheck: rejecting request because no Origin header found")
        false
    }

  /**
   * Returns true if the value of the Origin header contains an acceptable value.
   */
  private def originMatches(origin: String): Boolean =
    try {
      val url = new URI(origin)
      val port = url.getPort
      val host = url.getHost
      envOrNone("DEPLOY_HOST") match {
        case None =>
          host == "localhost" &&
          (port match {
            case 9000 | 8080 | 8081 | 19001 =>
              logger.LogInfo("SameOriginCheck", s"Testing $host == $port")
              true;
            case _ =>
              logger.LogFailure("SameOriginCheck", s"Connection failed host: $host port: $port")
              false
          })
        case Some(h) =>
          val test = host == h
          if (test) logger.LogInfo("SameOriginCheck", s"Testing origin $host == $h")
          else logger.LogInfo("SameOriginCheck", s"Test failed $host != $h")
          test
      }
    } catch {
      case e: Exception =>
        logger.LogFailure("SameOriginCheck", s"Connection failed host: ${e.getMessage}")
        false
    }
}
