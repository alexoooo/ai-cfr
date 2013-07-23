package ao.learn.mst.gen2.game

import ao.learn.mst.gen2.player.model.DeliberatePlayer
import ao.learn.mst.gen2.info.InformationSet


/**
 * Date: 03/12/11
 * Time: 7:29 PM
 */

trait ExtensiveGameDecision
    extends ExtensiveGameNonTerminal
{
  //--------------------------------------------------------------------------------------------------------------------
  /**
   * @return RationalPlayer, i.e. next to act.
   */
  override def player : DeliberatePlayer


  //--------------------------------------------------------------------------------------------------------------------
//  override def child(decision : FiniteAction) : ExtensiveGameNode


  //--------------------------------------------------------------------------------------------------------------------
  /**
   * @return InformationSet from the point of view of "player"
   */
  def informationSet : InformationSet
}