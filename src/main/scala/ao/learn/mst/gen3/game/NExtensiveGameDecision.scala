package ao.learn.mst.gen3.game

import ao.learn.mst.gen2.player.model.DeliberatePlayer
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen3.NExtensiveAction

/**
 * 09/06/13 10:03 PM
 */
trait NExtensiveGameDecision[+I <: InformationSet, +A <: NExtensiveAction]
  extends NExtensiveGameNonTerminal[I, A]
{
  //--------------------------------------------------------------------------------------------------------------------
  /**
   * @return RationalPlayer, i.e. next to act.
   */
  override def player : DeliberatePlayer


  //--------------------------------------------------------------------------------------------------------------------
  /**
   * @return InformationSet from the point of view of "player"
   */
  def informationSet : I
}