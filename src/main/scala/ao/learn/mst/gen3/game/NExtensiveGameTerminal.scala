package ao.learn.mst.gen3.game

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen3.NExtensiveAction

/**
 *
 */
trait NExtensiveGameTerminal[I <: InformationSet, A <: NExtensiveAction]
  extends NExtensiveGameNode[I, A]
{
  //--------------------------------------------------------------------------------------------------------------------
  override def actions = Set.empty


  //--------------------------------------------------------------------------------------------------------------------
  /** "Each terminal (leaf) node of the game tree has an n-tuple of payoffs,
    *    meaning there is one payoff for each player at the end of every possible play"
    */
  def payoff : NExpectedValue
}
