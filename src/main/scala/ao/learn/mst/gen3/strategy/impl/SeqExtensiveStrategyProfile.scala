package ao.learn.mst.gen3.strategy.impl

import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen.chance.ProbabilityMass


//----------------------------------------------------------------------------------------------------------------------
/**
 * todo: could handle information sets and action counts higher than given by falling back on default strategy
 *
 * @param probabilities information set index -> action index -> probability
 */
case class SeqExtensiveStrategyProfile(probabilities:Seq[Seq[Double]])
  extends ExtensiveStrategyProfile
{
  //--------------------------------------------------------------------------------------------------------------------
  def actionProbabilityMass(
      informationSetIndex:Int, actionCount:Int): ProbabilityMass =
  {
    val actionProbabilities : Seq[Double] =
      probabilities(informationSetIndex)

    if (actionProbabilities == null || actionProbabilities.length != actionCount) {
      throw new IllegalArgumentException("Unexpected number of actions: " +
        informationSetIndex + " | " + actionCount + " | " + actionProbabilities)
    }

    ProbabilityMass(actionProbabilities)
  }


  //--------------------------------------------------------------------------------------------------------------------
  def actionProbability(informationSetIndex: Int, actionIndex: Int): Double = {
    if (informationSetIndex >= probabilities.length) {
      return 0 // fallback action can be taken out
    }

    val actionProbabilities : Seq[Double] =
      probabilities(informationSetIndex)

    if (actionProbabilities == null || actionProbabilities.length != actionIndex) {
      return 0
//      throw new IllegalArgumentException("Unexpected action index: " +
//        informationSetIndex + " | " + actionIndex + " | " + actionProbabilities)
    }

    actionProbabilities(actionIndex)
  }
}
