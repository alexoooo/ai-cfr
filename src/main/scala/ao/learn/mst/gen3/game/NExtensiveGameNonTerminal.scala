package ao.learn.mst.gen3.game

import ao.learn.mst.gen2.player.model.PlayerIdentity
import ao.learn.mst.gen3.NExtensiveAction
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen3.example.NKuhnAction

/**
 * 09/06/13 9:58 PM
 */
trait NExtensiveGameNonTerminal[+I <: InformationSet, +A <: NExtensiveAction]
  extends NExtensiveGameNode[I, A]
{
  //--------------------------------------------------------------------------------------------------------------------
  def player : PlayerIdentity


  //--------------------------------------------------------------------------------------------------------------------
  def child[T >: A](action : T) : Option[NExtensiveGameNode[I, A]]
}
