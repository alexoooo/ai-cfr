package ao.learn.mst.gen2.game

import ao.learn.mst.gen2.prob.ActionProbabilityMass
import ao.learn.mst.gen2.player.model.{FiniteAction, ChancePlayer}

/**
 * Date: 03/12/11
 * Time: 7:27 PM
 */

trait ExtensiveGameChance
    extends ExtensiveGameNonTerminal
{
  //--------------------------------------------------------------------------------------------------------------------
  def player = ChancePlayer


  //--------------------------------------------------------------------------------------------------------------------
  def probabilityMass : ActionProbabilityMass


  //--------------------------------------------------------------------------------------------------------------------
  override def child(outcome : FiniteAction) : ExtensiveGameDecision
}