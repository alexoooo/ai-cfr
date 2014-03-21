package ao.learn.mst.gen5.state.regret.impl

import ao.learn.mst.gen5.state.impl.data.ArrayTableTally
import ao.learn.mst.gen5.state.regret.RegretStore
import java.io.File
import java.util

/**
 * 
 */
class ArrayRegretStore
    extends RegretStore
{
  //--------------------------------------------------------------------------------------------------------------------
  private val tally = new ArrayTableTally()


  //--------------------------------------------------------------------------------------------------------------------
  override def commit(accumulatedRegret: Map[Long, Seq[Double]]): Unit =
    tally.addAll(accumulatedRegret)


  override def cumulativeRegret(informationSetIndex: Long): IndexedSeq[Double] =
    tally.get(informationSetIndex).getOrElse(IndexedSeq.empty)


  //--------------------------------------------------------------------------------------------------------------------
  def write(filePath: File): Unit =
    tally.write(filePath)

  def read(filePath: File): Unit =
    tally.read(filePath)


  //--------------------------------------------------------------------------------------------------------------------
  override def equals(other: Any): Boolean =
    other match {
      case that: ArrayRegretStore =>
        tally == that.tally

      case _ => false
    }
}
