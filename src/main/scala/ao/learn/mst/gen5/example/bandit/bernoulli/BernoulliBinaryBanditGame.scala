package ao.learn.mst.gen5.example.bandit.bernoulli

import ao.learn.mst.gen5.example.bandit.{StochasticBinaryBanditGameCompanion, StochasticBinaryBanditGame}
import scala.util.Random

/**
 *
 */
case class BernoulliBinaryBanditGame(
    falseProbability : Double,
    trueProbability  : Double)
    (implicit sourceOfRandomness : Random)
  extends StochasticBinaryBanditGame(falseProbability, trueProbability)
{
  def payoff(probability : Double): Double = {
    val outcome : Boolean =
      sourceOfRandomness.nextDouble() < probability

    outcome match {
      case false => 0
      case true  => 1
    }
  }
}


object BernoulliBinaryBanditGame extends StochasticBinaryBanditGameCompanion[BernoulliBinaryBanditGame]
{
//  def withAdvantageForTrue(
//      probabilityAdvantage : Double)
//      (implicit sourceOfRandomness : Random)
//      : BernoulliBinaryBanditGame =
//  {
//    StochasticBinaryBanditGame.withAdvantageForTrue(
//      probabilityAdvantage,
//      (falseP: Double, trueP: Double) =>
//        BernoulliBinaryBanditGame(falseP, trueP))
//  }

  def build(
      falseProbability: Double, trueProbability: Double)
      (implicit sourceOfRandomness : Random) =
    BernoulliBinaryBanditGame(falseProbability, trueProbability)

}