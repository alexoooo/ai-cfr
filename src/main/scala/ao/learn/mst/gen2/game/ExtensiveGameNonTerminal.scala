package ao.learn.mst.gen2.game

import ao.learn.mst.gen2.player.model.{FiniteAction, PlayerIdentity}


/**
 * User: ao
 * Date: 11/03/12
 * Time: 2:39 PM
 */

trait ExtensiveGameNonTerminal
  extends ExtensiveGameNode
{
  //--------------------------------------------------------------------------------------------------------------------
  def player : PlayerIdentity


  //--------------------------------------------------------------------------------------------------------------------
  def child(action : FiniteAction) : ExtensiveGameNode
}
