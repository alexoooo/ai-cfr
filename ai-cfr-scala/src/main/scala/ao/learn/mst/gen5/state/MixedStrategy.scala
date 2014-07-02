package ao.learn.mst.gen5.state

import ao.learn.mst.gen5.ExtensiveAbstraction

/**
 * Mixed strategy with probabilities of performing each action for every information set.
 */
trait MixedStrategy
{
  def probabilities(informationSetIndex: Long, actionCount: Int): IndexedSeq[Double]


  //--------------------------------------------------------------------------------------------------------------------
  def probability[I, A](
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

    probabilities(infoIndex, actionCount)(actionIndex)
  }
}
