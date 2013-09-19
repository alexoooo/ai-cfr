package ao.learn.mst.gen5.example.bandit.gaussian

import scala.util.Random
import ao.learn.mst.gen5.example.bandit.{StochasticBinaryBanditGameCompanion, StochasticBinaryBanditGame}


/**
 *
 */
case class GaussianBinaryBanditGame(
    falseProbability : Double,
    trueProbability  : Double)
  (implicit sourceOfRandomness : Random)
  extends StochasticBinaryBanditGame(falseProbability, trueProbability)
{
  def payoff(probability : Double): Double = {
    val outcome : Double =
      sourceOfRandomness.nextGaussian() + probability

    outcome
  }
}


object GaussianBinaryBanditGame extends StochasticBinaryBanditGameCompanion[GaussianBinaryBanditGame]
{
  def build
  (falseProbability: Double, trueProbability: Double)
  (implicit sourceOfRandomness: Random) =
    GaussianBinaryBanditGame(falseProbability, trueProbability)
}