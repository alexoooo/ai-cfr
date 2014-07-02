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

    if (probabilitySums.isEmpty)
    {
      // default; todo: can this be eliminated?
      IndexedSeq.fill(actionCount)(1.0 / actionCount)
    }
    else
    {
      val strategyTotal: Double =
        probabilitySums.sum

      val averageStrategy: IndexedSeq[Double] =
        probabilitySums
          .map(_ / strategyTotal)

      require(averageStrategy.length <= actionCount)

      if (averageStrategy.length == actionCount) {
        averageStrategy
      } else {
        averageStrategy.padTo(actionCount, 0.0)
      }
    }
  }
}
