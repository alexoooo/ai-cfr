package ao.learn.mst.gen5.state.strategy.impl

import ao.learn.mst.gen5.state.impl.data.ArrayTableTally
import ao.learn.mst.gen5.state.strategy.StrategyStore
import java.io.File

/**
 *
 */
class ArrayStrategyStore
    extends StrategyStore
{
  private val tally = new ArrayTableTally


  override def commit(accumulatedStrategy: Map[Long, Seq[Double]]): Unit =
    tally.addAll(accumulatedStrategy)

  override def cumulativeStrategy(informationSetIndex: Long): IndexedSeq[Double] =
    tally.get(informationSetIndex).getOrElse(IndexedSeq.empty)


  def write(filePath: File): Unit =
    tally.write(filePath)

  def read(filePath: File): Unit =
    tally.read(filePath)
}
