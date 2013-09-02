package ao.learn.mst.gen5.example.abstraction

import ao.learn.mst.gen5.ExtensiveAbstraction

/**
 * Abstraction that can change over time
 */
trait AdaptiveAbstraction[InformationSet, Action]
{
  def learnActionTransition(
    informationSet : InformationSet,
    action         : Action)

  def generate : ExtensiveAbstraction[InformationSet, Action]
}
