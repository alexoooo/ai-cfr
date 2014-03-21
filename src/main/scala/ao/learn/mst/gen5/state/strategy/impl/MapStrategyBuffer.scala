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

  override def accumulated: Map[Long, IndexedSeq[Double]] =
    tallyBuffer.accumulated

  override def clear(): Unit =
    tallyBuffer.clear()
}
