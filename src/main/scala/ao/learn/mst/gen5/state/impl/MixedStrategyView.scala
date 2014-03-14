package ao.learn.mst.gen5.state.impl

import ao.learn.mst.gen5.state.MixedStrategy
import ao.learn.mst.gen5.state.strategy.StrategyAccumulator
import ao.learn.mst.gen5.state.regret.{RegretAccumulator, RegretMatcher}
import ao.learn.mst.gen5.state.strategy.impl.AverageStrategy

/**
 *
 */
class MixedStrategyView(
    regretMatcher: RegretMatcher,
    regretAccumulator: RegretAccumulator,
    strategyAccumulator: Option[StrategyAccumulator])
    extends MixedStrategy
{
  private val averageStrategy: Option[AverageStrategy] =
    strategyAccumulator.map(new AverageStrategy(_))


  override def probabilities(informationSetIndex: Long, actionCount: Int): IndexedSeq[Double] =
    averageStrategy
      .map(_.probabilities(informationSetIndex, actionCount))
      .getOrElse({
        val cumulativeRegret: Seq[Double] =
          regretAccumulator.cumulativeRegret(informationSetIndex)

        regretMatcher.positiveRegretStrategy(cumulativeRegret, actionCount)
      })
}
