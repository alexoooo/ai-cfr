package ao.learn.mst.gen3.strategy


trait CfrStrategyBuffer
{
  def bufferUpdate(
    informationSetIndex      : Int,
    actionRegret             : Seq[Double],
    opponentReachProbability : Double)

  def commit(
    cfrStrategyProfileBuilder : CfrStrategyProfile)
}

