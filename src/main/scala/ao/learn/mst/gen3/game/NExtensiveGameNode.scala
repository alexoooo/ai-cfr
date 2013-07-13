package ao.learn.mst.gen3.game

import ao.learn.mst.gen3.NExtensiveAction
import ao.learn.mst.gen2.info.InformationSet

/**
 * http://en.wikipedia.org/wiki/Extensive-form_game
 */
trait NExtensiveGameNode[I <: InformationSet, A <: NExtensiveAction]
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions : Set[A]
}