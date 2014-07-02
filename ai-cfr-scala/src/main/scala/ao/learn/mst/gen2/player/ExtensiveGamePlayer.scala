package ao.learn.mst.gen2.player

import ao.learn.mst.gen2.game.ExtensiveGameDecision
import ao.learn.mst.gen2.player.model.FiniteAction

/**
 * 25/04/13 8:02 PM
 */
trait ExtensiveGamePlayer
{
  /**
   * @param node node in which to select actions
   * @return action to take in given node
   */
  def selectAction(node:ExtensiveGameDecision):FiniteAction
}
