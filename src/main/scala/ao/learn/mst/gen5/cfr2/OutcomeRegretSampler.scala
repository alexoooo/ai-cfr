package ao.learn.mst.gen5.cfr2

import ao.learn.mst.gen5.solve2.{StrategyRegretSample, AbstractGame, RegretSampler}
import ao.learn.mst.gen5.state.regret.{RegretBuffer, RegretStore, RegretMatcher}
import scala.util.Random
import ao.learn.mst.lib.NumUtils
import ao.learn.mst.gen5.node.ExtensiveStateNode
import ao.learn.mst.gen5.state.regret.impl.{UniformDefaultRegretMatcher, MapRegretBuffer, ArrayRegretStore}
import ao.learn.mst.gen5.state.strategy.{StrategyBuffer, StrategyStore}
import ao.learn.mst.gen5.state.strategy.impl.{MapStrategyBuffer, ArrayStrategyStore}

/**
 *
 */
class OutcomeRegretSampler[State, InformationSet, Action](
    regretMatcher: RegretMatcher = UniformDefaultRegretMatcher,
    explorationProbability: Double = 0.6,
    allowSupersetAbstraction: Boolean = false,
    randomness: Random = new Random)
  extends RegretSampler[State, InformationSet, Action]
{
  //---------------------------------------------------------------------------------------------------------------------
  override def sampleRegret(
      game: AbstractGame[State, InformationSet, Action],
      player: Int)
      : Map[Long, Seq[Double]] =
  {
    val context = Context(
      game, player, new MapRegretBuffer, None)


    ???
  }

  override def sampleStrategyAndRegret(
      game: AbstractGame[State, InformationSet, Action],
      player: Int)
      : StrategyRegretSample =
  {
    val context = Context(
      game, player, new MapRegretBuffer, Some(new MapStrategyBuffer))

    ???
  }





  //----------------------------------------------------------------------------------------------------------------
  private def walkTree(
      context             : Context,
      stateNode           : ExtensiveStateNode[State, InformationSet, Action],
      reachProbabilities  : Seq[Double],
      sampleProbabilities : Seq[Double]
      ): SampleOutcome =
  {
    ???
  }


  //---------------------------------------------------------------------------------------------------------------------
  private def checkProbNotZero(probability: Double): Unit =
    assert(0 < probability && probability <= 1.0)

  private def omitMissingProbabilities(
      probabilities        : Seq[Double],
      missingProbabilities : Set[Int]): Seq[Double] =
  {
    if (missingProbabilities.isEmpty) {
      probabilities
    } else {
      val denormalized: Seq[Double] =
        probabilities.zipWithIndex
          .map(si => if (missingProbabilities.contains(si._2)) 0 else si._1)

      if (denormalized.exists(_ > 0)) {
        NumUtils.normalizeToOne(denormalized)
      } else {
        val equalProbability: Double =
          1.0 / (probabilities.size - missingProbabilities.size)

        (0 until probabilities.size)
          .map(i => if (missingProbabilities.contains(i)) 0 else equalProbability)
      }
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  private case class Context(
    game: AbstractGame[State, InformationSet, Action],
    player: Int,
    regretBuffer: RegretBuffer,
    strategyBuffer: Option[StrategyBuffer])


  private case class ActionSample(
      index: Int, probability: Double)
  {
    checkProbNotZero(probability)
  }

  private case class SampleOutcome(
      payoff: Double,
      sampleProbability: Double,
      suffixReachProbability: Double)
  {
    def scaleSuffixReach(moveProb: Double): SampleOutcome =
      copy(suffixReachProbability = suffixReachProbability * moveProb)
  }
}
