package ao.learn.mst.gen5.example.bandit

import scala.util.Random
import scala.Double

/**
 * 12/09/13 8:28 PM
 */
abstract class StochasticBinaryBanditGame(
    falseProbability : Double,
    trueProbability  : Double)
  extends BinaryBanditGame
{
  def payoff(choice: Boolean): Double = {
    val probability : Double =
      choice match {
        case false => falseProbability
        case true  => trueProbability
      }

    payoff(probability)
  }

  def payoff(probability: Double): Double
}


trait StochasticBinaryBanditGameCompanion[T <: StochasticBinaryBanditGame]
{
  def withAdvantageForTrue(
      probabilityAdvantage : Double)
    (implicit sourceOfRandomness : Random)
    : T =
  {
    assert(0 <= probabilityAdvantage && probabilityAdvantage <= 1, "Probability not in range: [0, 1]")

    val rangeOfRandomness : Double =
      1.0 - probabilityAdvantage

    val falseProbability : Double =
      rangeOfRandomness / 2

    val trueProbability : Double =
      falseProbability + probabilityAdvantage

    build(falseProbability, trueProbability)
  }


  def build(
    falseProbability: Double,
    trueProbability: Double)
    (implicit sourceOfRandomness : Random)
    : T
}