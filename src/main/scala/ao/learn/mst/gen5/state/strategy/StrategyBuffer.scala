package ao.learn.mst.gen5.state.strategy

/**
 *
 */
trait StrategyBuffer
    extends StrategyAccumulator
{
  def add(
    informationSetIndex: Long,
    currentPositiveRegretStrategy: Seq[Double],
    externalReachProbability: Double): Unit

  def accumulated: Map[Long, IndexedSeq[Double]]

  def clear(): Unit


  override def cumulativeStrategy(informationSetIndex: Long): IndexedSeq[Double] =
    accumulated.get(informationSetIndex).getOrElse(IndexedSeq.empty)


  def flush(regretStore: StrategyStore): Unit = {
    regretStore.commit(accumulated)
    clear()
  }
}
