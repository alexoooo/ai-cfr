package ao.learn.mst.gen5.example.bandit.uniform

import scala.util.Random
import ao.learn.mst.gen5.example.bandit.{StochasticBinaryBanditGameCompanion, StochasticBinaryBanditGame}


/**
 * Uniform distribution
 */
case class UniformBinaryBanditGame(
    falseProbability : Double,
    trueProbability  : Double)
  (implicit sourceOfRandomness : Random)
  extends StochasticBinaryBanditGame(falseProbability, trueProbability)
{
  def payoff(probability : Double): Double = {
    val outcome : Double =
      sourceOfRandomness.nextDouble() * probability

    outcome
  }
}


object UniformBinaryBanditGame extends StochasticBinaryBanditGameCompanion[UniformBinaryBanditGame]
{
  def build
      (falseProbability: Double, trueProbability: Double)
      (implicit sourceOfRandomness: Random) =
    UniformBinaryBanditGame(falseProbability, trueProbability)
}