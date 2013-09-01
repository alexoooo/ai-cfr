package ao.learn.mst.gen5.example

import ao.learn.mst.gen5.ExtensivePlayer
import scala.util.Random



class RandomPlayer[InformationSet, Action](
  sourceOfRandomness : Random)
  extends ExtensivePlayer[InformationSet, Action]
{
  def act(informationSet: InformationSet, actions: Traversable[Action]): Action =
  {
    val randomlyWeightedActions : Traversable[(Action, Double)] =
      actions.map((_, sourceOfRandomness.nextDouble()))

    val maximumWeightAction =
      randomlyWeightedActions.maxBy(_._2)._1

    maximumWeightAction
  }
}
