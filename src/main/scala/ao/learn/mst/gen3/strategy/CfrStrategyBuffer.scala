package ao.learn.mst.gen3.strategy

/**
 * 20/06/13 9:39 PM
 */
trait CfrStrategyBuffer
{
  def bufferUpdate(
    informationSetIndex : Int,
    actionRegret        : Seq[Double],
    reachProbability    : Double)

  def commit(
    cfrStrategyProfileBuilder : CfrStrategyProfile)
}

