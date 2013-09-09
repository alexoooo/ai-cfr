package ao.learn.mst.gen3.strategy

import ao.learn.mst.gen.chance.ProbabilityMass

/**
 * 20/06/13 9:35 PM
 */
trait ExtensiveStrategyProfile
{
  def actionProbabilityMass(
    informationSetIndex:Int,
    actionCount:Int
    ): ProbabilityMass

  def actionProbability(
    informationSetIndex:Int,
    actionCount:Int
    ): Double
}
