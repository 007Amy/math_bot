package model.models

import com.github.pdorobisz.mathexpressionevaluator.Evaluator
import play.api.libs.json.{JsPath, Json, Reads}
import spire.math.Rational
import utils.Encryption._

import scala.util.Random

case class Problem(problem: String, encryptedProblem: String)

object Problem {

  def apply(problem: String): Problem = new Problem(problem, encrypt("PROBLEM", problem))

  implicit val jsonFormat = Json.format[Problem]

  implicit val problemReads: Reads[Problem] = (JsPath \ "problem").read[String].map(p => Problem.apply(p))

  private def genRandom(length: Int) = {

    def rand(s: Int, e: Int) = s + Random.nextInt((e - s) + 1)

    length match {
      case 1 => rand(1, 9)
      case 2 => rand(10, 99)
      case 3 => rand(100, 999)
      case 4 => rand(1000, 9999)
      case 5 => rand(10000, 99999)
      case 6 => rand(100000, 999999)
      case _ => 0
    }
  }

  private def toInt(s: Char): Int =
    try {
      s.asDigit
    } catch {
      case _: Exception => 0
    }

  /*
   * @def makeProblem takes a string representation of the problem
   * @param prob is the problem
   *   e.g --> "{1} + {2} * ({6} - {4}) - 55"
   *   - numbers wrapped in { } refer to how many digits the number needs to be in length
   *   - plain numbers will be left as part of the equation
   *
   * @output "4 + 65 * (123456 - 4513) - 55"
   * */
  def makeProblem(prob: String): Problem = {
    val p = "\\{(.*?)\\}".r.replaceAllIn(prob, l => {
      val prepL = toInt(l.toString.charAt(1))
      genRandom(prepL).toString
    })

    val validateP = Evaluator.evaluate(p)

    validateP.isSuccess match {
      case true => this.apply(p, encrypt("PROBLEM", p))
      case false => this.apply(s"$p is not a valid problem.", encrypt("PROBLEM", p))
    }
  }

  /*
   * @def evalProblem
   * @param prob == Problem(String)
   *
   * @output
   *   - if valid --> Rational(solution)
   *   - else Exception(Rational("This is why input is invalid"))
   * */
  def evalProblem(prob: Problem): Rational = {
    val e = Evaluator.evaluate(decrypt("PROBLEM", prob.problem))
    e.isSuccess match {
      case true => e.toList.head
      case false => throw new Exception("invalid expression --> " + e.toString)
    }
  }
}
