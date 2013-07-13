package ao.learn.mst.example.perfect.complete

import ao.learn.mst.gen2.game.ExtensiveGame
import decision.PerfectCompleteDecisionFirst

/**
 * Fully deterministic, 2 players, each gets 1 binary choice.
 *
 * Game tree for at:
 *  http://en.wikipedia.org/wiki/Extensive-form_game#Perfect_and_complete_information
 *
 * Perfect play (where U = first action (index 0), and D = second action (index 1)):
 *  Player 1 will play U and then
 *    player 2 plays D.
 *  If Player 1 plays D (sub-optimal), then
 *    player 2 plays U.
 */
object PerfectCompleteGame
  extends ExtensiveGame
{
  //--------------------------------------------------------------------------------------------------------------------
  def rationalPlayerCount = 2


  //--------------------------------------------------------------------------------------------------------------------
  def treeRoot =
    PerfectCompleteDecisionFirst
}
