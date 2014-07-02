package ao.learn.mst.gen5.state.regret


/**
 *
 */
trait RegretStore
    extends RegretAccumulator
{
  def commit(accumulatedRegret: Map[Long, Seq[Double]]): Unit
}
