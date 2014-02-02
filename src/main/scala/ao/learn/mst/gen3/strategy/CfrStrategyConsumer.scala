package ao.learn.mst.gen3.strategy

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
