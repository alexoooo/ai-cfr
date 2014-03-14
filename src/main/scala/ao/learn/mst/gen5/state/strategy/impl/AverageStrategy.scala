package ao.learn.mst.gen5.state.strategy.impl

import ao.learn.mst.gen5.state.MixedStrategy
import ao.learn.mst.gen5.state.strategy.StrategyAccumulator

/**
 *
 */
class AverageStrategy(strategyAccumulator: StrategyAccumulator)
    extends MixedStrategy
{
  override def probabilities(informationSetIndex: Long, actionCount: Int): IndexedSeq[Double] = {
    val probabilitySums: IndexedSeq[Double] =
      strategyAccumulator.cumulativeStrategy(informationSetIndex)

    val strategyTotal: Double =
      probabilitySums.sum

    val averageStrategy : IndexedSeq[Double] =
      probabilitySums
        .map(_ / strategyTotal)

    averageStrategy
  }
}
