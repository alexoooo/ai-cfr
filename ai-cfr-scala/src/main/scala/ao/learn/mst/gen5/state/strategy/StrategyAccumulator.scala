package ao.learn.mst.gen5.state.strategy

/**
 *
 */
trait StrategyAccumulator
{
  /**
   * @param informationSetIndex abstract information set index
   * @return action probability sums, or empty if infoSetIndex is unknown
   */
  def cumulativeStrategy(informationSetIndex: Long): IndexedSeq[Double]
}
