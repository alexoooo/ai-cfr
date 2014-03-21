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


  override def add(informationSetIndex: Long, regret: Seq[Double]): Unit =
    tallyBuffer.add(informationSetIndex, regret)


  override def clear(): Unit =
    tallyBuffer.clear()


  override def accumulated: Map[Long, IndexedSeq[Double]] =
    tallyBuffer.accumulated
}
