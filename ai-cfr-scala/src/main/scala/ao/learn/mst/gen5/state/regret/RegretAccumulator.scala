package ao.learn.mst.gen5.state.regret

/**
 *
 */
trait RegretAccumulator
{
  /**
   * @param informationSetIndex abstract information set index
   * @return action regret sum, or empty if informationSetIndex is unknown
   */
  def cumulativeRegret(informationSetIndex: Long): IndexedSeq[Double]
}
