package ao.learn.mst.gen3.strategy

import ao.learn.mst.gen.chance.ProbabilityMass

/**
 * 20/06/13 9:35 PM
 */
trait CfrStrategyProfile
{
  def toExtensiveStrategyProfile: ExtensiveStrategyProfile

  def update(
    informationSetIndex : Int,
    actionRegret        : Seq[Double],
    reachProbability    : Double)

  def positiveRegretStrategy(
    informationSetIndex:Int,
    actionCount:Int
  ): ProbabilityMass
}
