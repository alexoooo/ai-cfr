package ao.learn.mst.gen

import act.ExtensiveAction
import chance.ProbabilityMass
import history.ExtensiveInformationPartition
import player.{Rational, ExtensivePlayerPartition, ExtensivePlayerPosition}
import utility.ExtensivePayoff

/**
 * http://en.wikipedia.org/wiki/Extensive-form_game
 *
 * Chance nodes are chained for simultaneous information revealing with observableOnlyTo().
 */
trait ExtensiveGameNodeX
{
  //--------------------------------------------------------------------------------------------------------------------
  def childCount : Int
  def isTerminal = (childCount == 0)

  /** "Each terminal (leaf) node of the game tree has an n-tuple of payoffs,
    *    meaning there is one payoff for each player at the end of every possible play"  */
  def payoff : Option[ExtensivePayoff]


  //--------------------------------------------------------------------------------------------------------------------
  /**"A partition of the non-terminal nodes of the game tree in n+1 subsets,
   *    one for each (rational) player, and with a special subset for a fictitious
   *    player called Chance (or Nature). Each player's subset of nodes is referred
   *    to as the "nodes of the player".
   *    (A game of complete information thus has an empty set of Chance nodes.)" */
  def playerPartition : Option[ExtensivePlayerPartition]

  final def nextToAct = playerPartition

  final def nextToActIndex : Option[Int] = nextToAct match {
      case Some(rational : Rational) => Some(rational.position.absolutePositionIndex)
      case _ => None
    }

//    nextToAct.map(i : ExtensivePlayerPartition => i)
  //case Rational(ExtensivePlayerPosition(_)) => _


  //--------------------------------------------------------------------------------------------------------------------
  /** "Each node of the Chance player has a probability distribution over its outgoing edges." */
  def childProbabilities : Option[ProbabilityMass]


  //--------------------------------------------------------------------------------------------------------------------
  /** Some chance player actions (private cards) are only visible to specific players.
   *  If None, then the available actions will be visible to all players */
  def observableOnlyTo : Option[ExtensivePlayerPosition]

  /** This is what information sets are groups by. */
  def informationPartition : Seq[ExtensiveInformationPartition]


  //--------------------------------------------------------------------------------------------------------------------
  def child(index : ExtensiveAction) : Option[ExtensiveGameNodeX]
}