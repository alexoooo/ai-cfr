package ao.learn.mst.gen5.strategy.impl

import ao.learn.mst.gen5.strategy.ExtensiveStrategyProfile
import ao.learn.mst.lib.CommonUtils


//----------------------------------------------------------------------------------------------------------------------
/**
 * todo: could handle information sets and action counts higher than given by falling back on default strategy
 *
 * @param probabilities information set index -> action index -> probability
 */
case class SeqExtensiveStrategyProfile(
  probabilities : Seq[Seq[Double]],
  defaultStrategy : ExtensiveStrategyProfile = ObliviousExtensiveStrategyProfile)
  extends ExtensiveStrategyProfile
{
  //--------------------------------------------------------------------------------------------------------------------
  def knownInformationSetCount: Int =
    probabilities.length


  //--------------------------------------------------------------------------------------------------------------------
  def actionProbabilityMass(informationSetIndex: Int): Seq[Double] = {
    if (informationSetIndex >= knownInformationSetCount) {
      return defaultStrategy.actionProbabilityMass(informationSetIndex)
    }

    val actionProbabilities : Seq[Double] =
      probabilities(informationSetIndex)

    actionProbabilities
  }


  //--------------------------------------------------------------------------------------------------------------------
  def actionProbabilityMass(
      informationSetIndex:Int, actionCount:Int): Seq[Double] =
  {
    assert(actionCount >= 1)

    if (informationSetIndex >= knownInformationSetCount) {
      return defaultStrategy.actionProbabilityMass(informationSetIndex, actionCount)
    }

    val actionProbabilities: Seq[Double] =
      probabilities(informationSetIndex)

    if (actionProbabilities.isEmpty) {
      defaultStrategy.actionProbabilityMass(informationSetIndex, actionCount)
    } else if (actionProbabilities.length != actionCount) {
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


  //--------------------------------------------------------------------------------------------------------------------
  override def toString : String = {
    val infoSets : Seq[String] =
      for (infoSet <- probabilities) yield {
        CommonUtils.displayProbabilities(infoSet)
      }

    infoSets.mkString(" || ")
  }
}
