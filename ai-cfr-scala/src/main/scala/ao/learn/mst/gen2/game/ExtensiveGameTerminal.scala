package ao.learn.mst.gen2.game

import ao.learn.mst.gen2.player.model.FiniteAction
import ao.learn.mst.gen2.solve.ExpectedValue
import collection.immutable.SortedSet

/**
 * Date: 03/12/11
 * Time: 7:25 PM
 */

trait ExtensiveGameTerminal
    extends ExtensiveGameNode
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions = SortedSet[FiniteAction]()


  //--------------------------------------------------------------------------------------------------------------------
  /** "Each terminal (leaf) node of the game tree has an n-tuple of payoffs,
   *    meaning there is one payoff for each player at the end of every possible play"
   */
  def payoff : ExpectedValue
}