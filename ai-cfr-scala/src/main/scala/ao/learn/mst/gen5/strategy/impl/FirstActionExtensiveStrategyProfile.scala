package ao.learn.mst.gen5.strategy.impl

import ao.learn.mst.gen5.strategy.ExtensiveStrategyProfile

/**
 *
 */
object FirstActionExtensiveStrategyProfile
  extends ExtensiveStrategyProfile
{
  def size: Long =
    0

  def actionProbabilityMass(informationSetIndex: Long): Seq[Double] =
    Seq(1.0)

  def actionProbabilityMass(informationSetIndex: Long, actionCount: Int): Seq[Double] =
    Seq(1.0).padTo(actionCount, 0.0)
}
