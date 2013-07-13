package ao.learn.mst.gen3.strategy

import ao.learn.mst.gen.chance.ProbabilityMass

/**
 * 20/06/13 9:35 PM
 */
trait ExtensiveStrategyProfile
{
  def actionProbabilities(
    informationSetIndex:Int,
    actionCount:Int
  ): ProbabilityMass
}
