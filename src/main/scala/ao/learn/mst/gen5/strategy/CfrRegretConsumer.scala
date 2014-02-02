package ao.learn.mst.gen5.strategy


/**
 *
 */
trait CfrRegretConsumer
{
  //--------------------------------------------------------------------------------------------------------------------
  def update(informationSetIndex: Int, actionRegret: Seq[Double], opponentReachProbability: Double)

  def update(informationSetIndex: Int, counterfactualRegret: Seq[Double])
}
