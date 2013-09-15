package ao.learn.mst.gen3.strategy.impl

import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen.chance.ProbabilityMass


//----------------------------------------------------------------------------------------------------------------------
/**
 * todo: could handle information sets and action counts higher than given by falling back on default strategy
 *
 * @param probabilities information set index -> action index -> probability
 */
case class SeqExtensiveStrategyProfile(
  probabilities:Seq[Seq[Double]])
  (implicit defaultStrategy : ExtensiveStrategyProfile = ObliviousExtensiveStrategyProfile)
  extends ExtensiveStrategyProfile
{
  //--------------------------------------------------------------------------------------------------------------------
  def actionProbabilityMass(
      informationSetIndex:Int, actionCount:Int): Seq[Double] =
  {
    if (informationSetIndex >= probabilities.length) {
      return defaultStrategy.actionProbabilityMass(informationSetIndex, actionCount)
    }

    val actionProbabilities : Seq[Double] =
      probabilities(informationSetIndex)

    if (actionProbabilities.length != actionCount) {
      actionProbabilities.padTo(actionCount, 0.0)
    } else {
      actionProbabilities
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
//  def actionProbability(informationSetIndex: Int, actionIndex: Int): Double = {
//    if (informationSetIndex >= probabilities.length) {
//      return defaultStrategy.actionProbability(informationSetIndex, actionIndex)
//    }
//
//    val actionProbabilities : Seq[Double] =
//      probabilities(informationSetIndex)
//
//    if (actionProbabilities == null || actionProbabilities.length <= actionIndex) {
//      return 0
////      throw new IllegalArgumentException("Unexpected action index: " +
////        informationSetIndex + " | " + actionIndex + " | " + actionProbabilities)
//    }
//
//    actionProbabilities(actionIndex)
//  }
}
