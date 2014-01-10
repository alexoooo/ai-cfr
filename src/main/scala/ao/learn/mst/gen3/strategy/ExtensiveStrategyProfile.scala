package ao.learn.mst.gen3.strategy

import ao.learn.mst.gen5.ExtensiveAbstraction


/**
 * Mixed strategy with probabilities of performing each action for every information set.
 */
trait ExtensiveStrategyProfile
{
  //--------------------------------------------------------------------------------------------------------------------
  def knownInformationSetCount: Int

  def actionProbabilityMass(
    informationSetIndex:Int) : Seq[Double]

  def actionProbabilityMass(
    informationSetIndex:Int,
    actionCount:Int
    ): Seq[Double]


  //--------------------------------------------------------------------------------------------------------------------
  def actionProbability[I, A](
    abstraction    : ExtensiveAbstraction[I, A],
    informationSet : I,
    action         : A)
    : Double =
  {
    val infoIndex : Int =
      abstraction.informationSetIndex(informationSet)

    val actionCount : Int =
      abstraction.actionCount(informationSet)

    val actionIndex : Int =
      abstraction.actionSubIndex(informationSet, action)

    actionProbabilityMass(infoIndex, actionCount)(actionIndex)
  }
}
