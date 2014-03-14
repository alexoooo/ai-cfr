package ao.learn.mst.gen5.state.regret.impl

import ao.learn.mst.gen5.state.impl.data.MapTallyBuffer
import ao.learn.mst.gen5.state.regret.{RegretStore, RegretBuffer}

/**
 *
 */
class MapRegretBuffer
  extends RegretBuffer
{
  private val tallyBuffer = new MapTallyBuffer


  override def add(informationSetIndex: Long, regret: Seq[Double]): Unit = {
    tallyBuffer.add(informationSetIndex, regret)
  }


  override def flush(regretStore: RegretStore): Unit = {
    regretStore.commit(tallyBuffer.getAll)
    tallyBuffer.clear()
  }


  override def cumulativeRegret(informationSetIndex: Long): IndexedSeq[Double] =
    tallyBuffer.get(informationSetIndex).getOrElse(IndexedSeq.empty)
}
