package ao.learn.mst.gen2.player.impl

import ao.learn.mst.gen2.player.ExtensiveGamePlayer
import ao.learn.mst.gen2.game.ExtensiveGameDecision
import ao.learn.mst.gen2.player.model.FiniteAction
import scala.util.Random

/**
 * 25/04/13 8:15 PM
 */
case object RandomExtensiveGamePlayer
    extends ExtensiveGamePlayer
{
  def selectAction(node: ExtensiveGameDecision): FiniteAction = {
    val actionsInRandomOrder =
      Random.shuffle(node.actions.toList)

    actionsInRandomOrder(0)
  }
}
