package ao.learn.mst.gen5.state.regret

/**
 *
 */
trait RegretBuffer
    extends RegretAccumulator
{
  def add(informationSetIndex: Long, regret: Seq[Double]): Unit
  
  def accumulated: Map[Long, IndexedSeq[Double]]
  
  def clear(): Unit


  override def cumulativeRegret(informationSetIndex: Long): IndexedSeq[Double] =
    accumulated.get(informationSetIndex).getOrElse(IndexedSeq.empty)
  

  def flush(regretStore: RegretStore): Unit = {
    regretStore.commit(accumulated)
    clear()
  }
}
