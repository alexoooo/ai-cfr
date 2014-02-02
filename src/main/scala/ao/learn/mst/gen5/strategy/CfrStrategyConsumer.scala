package ao.learn.mst.gen5.strategy

/**
 *
 */
trait CfrStrategyConsumer
{
  def update(
    informationSetIndex: Int,
    currentPositiveRegretStrategy: Seq[Double],
    externalReachProbability: Double) : Unit
}
