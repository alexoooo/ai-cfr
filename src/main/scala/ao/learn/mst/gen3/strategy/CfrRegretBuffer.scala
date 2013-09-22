package ao.learn.mst.gen3.strategy


trait CfrRegretBuffer
{
  def bufferRegret(
    informationSetIndex      : Int,
    actionRegret             : Seq[Double],
    opponentReachProbability : Double)

  def commit(
    cfrStrategyProfileBuilder : CfrRegretConsumer)
}
