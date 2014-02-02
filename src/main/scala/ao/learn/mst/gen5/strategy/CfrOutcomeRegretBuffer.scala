package ao.learn.mst.gen5.strategy

/**
 *
 */
trait CfrOutcomeRegretBuffer
{
  def bufferRegret(
      informationSetIndex  : Int,
      counterfactualRegret : Seq[Double])

  def commit(cfrStrategyProfileBuilder : CfrRegretConsumer)
}

