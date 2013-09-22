package ao.learn.mst.gen3.strategy


/**
 *
 */
trait CfrRegretConsumer
{
  //--------------------------------------------------------------------------------------------------------------------
  def update(informationSetIndex: Int, actionRegret: Seq[Double], opponentReachProbability: Double)
}
