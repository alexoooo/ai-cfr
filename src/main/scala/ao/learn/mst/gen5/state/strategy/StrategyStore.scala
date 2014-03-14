package ao.learn.mst.gen5.state.strategy


/**
 *
 */
trait StrategyStore
    extends StrategyAccumulator
{
  def commit(accumulatedStrategy: Map[Long, Seq[Double]]): Unit
}
