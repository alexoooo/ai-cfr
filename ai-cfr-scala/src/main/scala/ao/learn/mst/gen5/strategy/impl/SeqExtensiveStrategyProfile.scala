package ao.learn.mst.gen5.strategy.impl

import ao.learn.mst.gen5.strategy.ExtensiveStrategyProfile
import ao.learn.mst.lib.DisplayUtils


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
  def size: Long =
    probabilities.length


  //--------------------------------------------------------------------------------------------------------------------
  override def actionProbabilityMass(informationSetIndex: Long): Seq[Double] = {
    if (informationSetIndex >= size) {
      return defaultStrategy.actionProbabilityMass(informationSetIndex)
    }

    val actionProbabilities : Seq[Double] =
      probabilities(informationSetIndex.toInt)

    actionProbabilities
  }


  //--------------------------------------------------------------------------------------------------------------------
  override def actionProbabilityMass(
      informationSetIndex: Long, actionCount: Int): Seq[Double] =
  {
    assert(actionCount >= 1)

    if (informationSetIndex >= size) {
      return defaultStrategy.actionProbabilityMass(informationSetIndex, actionCount)
    }

    val actionProbabilities: Seq[Double] =
      probabilities(informationSetIndex.toInt)

    if (actionProbabilities.isEmpty) {
      defaultStrategy.actionProbabilityMass(informationSetIndex, actionCount)
    } else if (actionProbabilities.length < actionCount) {
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
        DisplayUtils.displayProbabilities(infoSet)
      }

    infoSets.mkString(" || ")
  }
}
