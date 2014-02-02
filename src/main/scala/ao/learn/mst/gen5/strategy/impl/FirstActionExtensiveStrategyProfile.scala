package ao.learn.mst.gen5.strategy.impl

import ao.learn.mst.gen5.strategy.ExtensiveStrategyProfile

/**
 *
 */
object FirstActionExtensiveStrategyProfile
  extends ExtensiveStrategyProfile
{
  def knownInformationSetCount: Int =
    0

  def actionProbabilityMass(informationSetIndex: Int): Seq[Double] =
    Seq(1.0)

  def actionProbabilityMass(informationSetIndex: Int, actionCount: Int): Seq[Double] =
    Seq(1.0).padTo(actionCount, 0.0)
}
