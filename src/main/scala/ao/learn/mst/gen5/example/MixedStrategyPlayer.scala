package ao.learn.mst.gen5.example

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensivePlayer}
import scala.util.Random
import ao.learn.mst.gen5.solve.MixedStrategy


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

      def indexToProbability(index : Int) : Double =
        mixedStrategy.actionProbability(informationSetIndex, index)

      def randomizedWeight(index : Int) : Double =
        indexToProbability(index) * sourceOfRandomness.nextDouble()

      indexToActions.keySet.maxBy(randomizedWeight)
    }

    val concreteActions : Seq[Action] =
      indexToActions(randomizedActionIndex).toSeq

    val concreteAction : Action =
      sourceOfRandomness.shuffle(concreteActions).head

    concreteAction
  }
}
