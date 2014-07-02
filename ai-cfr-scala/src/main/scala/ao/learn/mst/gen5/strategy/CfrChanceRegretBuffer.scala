package ao.learn.mst.gen5.strategy


trait CfrChanceRegretBuffer
{
  def bufferRegret(
    informationSetIndex      : Int,
    actionRegret             : Seq[Double],
    opponentReachProbability : Double)

  def commit(
    cfrStrategyProfileBuilder : CfrRegretConsumer)
}

