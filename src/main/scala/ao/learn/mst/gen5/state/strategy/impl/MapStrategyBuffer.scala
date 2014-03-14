package ao.learn.mst.gen5.state.strategy.impl

import ao.learn.mst.gen5.state.impl.data.MapTallyBuffer
import ao.learn.mst.gen5.state.strategy.{StrategyStore, StrategyBuffer}

/**
 *
 */
class MapStrategyBuffer
  extends StrategyBuffer
{
  private val tallyBuffer = new MapTallyBuffer


  override def add(
      informationSetIndex: Long,
      currentPositiveRegretStrategy: Seq[Double],
      externalReachProbability: Double): Unit =
  {
    val strategy: Seq[Double] =
      currentPositiveRegretStrategy.map(_ * externalReachProbability)

    tallyBuffer.add(informationSetIndex, strategy)
  }


  override def cumulativeStrategy(informationSetIndex: Long): IndexedSeq[Double] =
    tallyBuffer.get(informationSetIndex).getOrElse(IndexedSeq.empty)


  override def flush(regretStore: StrategyStore): Unit = {
    regretStore.commit(tallyBuffer.getAll)
    tallyBuffer.clear()
  }
}
