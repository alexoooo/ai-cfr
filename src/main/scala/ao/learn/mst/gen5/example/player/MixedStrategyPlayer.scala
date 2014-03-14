package ao.learn.mst.gen5.example.player

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensivePlayer}
import scala.util.Random
import ao.learn.mst.gen5.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.state.MixedStrategy


class MixedStrategyPlayer[InformationSet, Action](
  mixedStrategy : MixedStrategy,
  gameAbstraction : ExtensiveAbstraction[InformationSet, Action],
  sourceOfRandomness : Random)
  extends ExtensivePlayer[InformationSet, Action]
{
  def act(informationSet: InformationSet, actions: Traversable[Action]): Action =
  {
    val indexToActions : Map[Int, Traversable[Action]] = {
      def actionToIndex(action : Action) : Int =
        gameAbstraction.actionSubIndex(informationSet, action)

      actions.groupBy(actionToIndex)
    }

    val randomizedActionIndex : Int = {
      val informationSetIndex = gameAbstraction.informationSetIndex(informationSet)

      val actionIndexes : Set[Int] =
        indexToActions.keySet

      val actionCount : Int =
        actionIndexes.max + 1

      val indexToProbability: Seq[Double] =
        mixedStrategy.probabilities(informationSetIndex, actionCount)

      def randomizedWeight(index : Int) : Double =
        indexToProbability(index) * sourceOfRandomness.nextDouble()

      actionIndexes.maxBy(randomizedWeight)
    }

    val concreteActions : Seq[Action] =
      indexToActions(randomizedActionIndex).toSeq

    val concreteAction : Action =
      sourceOfRandomness.shuffle(concreteActions).head

    concreteAction
  }
}
