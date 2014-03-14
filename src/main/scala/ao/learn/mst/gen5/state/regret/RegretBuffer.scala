package ao.learn.mst.gen5.state.regret

/**
 *
 */
trait RegretBuffer
    extends RegretAccumulator
{
  def add(informationSetIndex: Long, regret: Seq[Double]): Unit

  def flush(regretStore: RegretStore): Unit
}
