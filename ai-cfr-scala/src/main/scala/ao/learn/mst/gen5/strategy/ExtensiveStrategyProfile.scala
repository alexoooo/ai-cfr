package ao.learn.mst.gen5.strategy

import ao.learn.mst.gen5.ExtensiveAbstraction
import ao.learn.mst.gen5.state.MixedStrategy


/**
 * Mixed strategy with probabilities of performing each action for every information set.
 */
trait ExtensiveStrategyProfile
    extends MixedStrategy
{
  //--------------------------------------------------------------------------------------------------------------------
  def size: Long

  def actionProbabilityMass(
    informationSetIndex: Long): Seq[Double]

  def actionProbabilityMass(
    informationSetIndex: Long,
    actionCount: Int
    ): Seq[Double]


  //--------------------------------------------------------------------------------------------------------------------
  override def probabilities(informationSetIndex: Long, actionCount: Int): IndexedSeq[Double] =
    actionProbabilityMass(informationSetIndex, actionCount).toIndexedSeq
}
