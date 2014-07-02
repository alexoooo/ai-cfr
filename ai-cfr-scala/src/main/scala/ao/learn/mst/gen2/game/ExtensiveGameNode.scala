package ao.learn.mst.gen2.game

import ao.learn.mst.gen2.player.model.FiniteAction
import collection.immutable.SortedSet

/**
 * http://en.wikipedia.org/wiki/Extensive-form_game
 */
trait ExtensiveGameNode
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions : SortedSet[FiniteAction]
}