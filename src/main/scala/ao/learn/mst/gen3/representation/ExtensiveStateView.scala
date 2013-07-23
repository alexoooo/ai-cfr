package ao.learn.mst.gen3.representation

import ao.learn.mst.gen2.player.model.DeliberatePlayer
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen3.NExtensiveAction

/**
 * 09/06/13 10:19 PM
 */
trait ExtensiveStateView[I <: InformationSet, A <: NExtensiveAction]
{
  def viewer: DeliberatePlayer

  def informationSet: I

  def actions : Set[A]
}
