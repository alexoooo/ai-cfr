package ao.learn.mst.gen3.strategy.impl

import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen.chance.ProbabilityMass

/**
 * 20/06/13 9:40 PM
 */
case class SeqExtensiveStrategyProfile(probabilities:Seq[Seq[Double]])
  extends ExtensiveStrategyProfile
{
  def actionProbabilities(
      informationSetIndex:Int, actionCount:Int): ProbabilityMass =
  {
    val actionProbabilities:Seq[Double] = probabilities(informationSetIndex)

    if (actionProbabilities == null || actionProbabilities.length != actionCount) {
      throw new IllegalArgumentException("Unexpected number of actions: " +
        informationSetIndex + " | " + actionCount + " | " + actionProbabilities)
    }

    ProbabilityMass(actionProbabilities)
  }
}
