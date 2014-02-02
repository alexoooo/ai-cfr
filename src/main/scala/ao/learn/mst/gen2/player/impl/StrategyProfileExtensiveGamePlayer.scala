package ao.learn.mst.gen2.player.impl

import ao.learn.mst.gen2.player.ExtensiveGamePlayer
import ao.learn.mst.gen2.game.ExtensiveGameDecision
import ao.learn.mst.gen2.player.model.FiniteAction
import ao.learn.mst.gen2.cfr.StrategyProfile
import scala.util.Random

/**
 * 25/04/13 10:04 PM
 */
case class StrategyProfileExtensiveGamePlayer(
    strategyProfile:StrategyProfile)
    extends ExtensiveGamePlayer
{
  /**
   * @param node node in which to select actions
   * @return action to take in given node
   */
  def selectAction(node: ExtensiveGameDecision): FiniteAction = {
    val allActionProbabilities:Map[FiniteAction, Double] =
      strategyProfile.averageStrategy(node.informationSet)

    val actions:Set[FiniteAction] =
      node.actions

    val availableActionProbabilities:Map[FiniteAction, Double] =
      allActionProbabilities.filter(
        (e: (FiniteAction, Double)) =>
          actions.contains(e._1))

    val actionPosteriors:Map[FiniteAction, Double] =
      availableActionProbabilities.transform(
        (action: FiniteAction, probability: Double) =>
          Random.nextDouble() * probability)

    val actionWithGreatestPosterior:FiniteAction =
      actionPosteriors.maxBy(_._2)._1

    actionWithGreatestPosterior
  }
}
