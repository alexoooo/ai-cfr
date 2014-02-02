package ao.learn.mst.gen2.example.imperfect.complete

import ao.learn.mst.gen2.game.ExtensiveGame
import decision.ImperfectCompleteDecisionFirst

/**
 *
 * Optimal strategy:
 *  player 1 plays D and
 *    player 2 plays U
 *    (and player 2 holds the belief that player 1 will definitely play D).
 *
 * Game tree:
 *  http://en.wikipedia.org/wiki/Extensive-form_game#Imperfect_information
 */

object ImperfectCompleteGame
  extends ExtensiveGame
{
  //--------------------------------------------------------------------------------------------------------------------
  val rationalPlayerCount = 2


  //--------------------------------------------------------------------------------------------------------------------
  val treeRoot =
    ImperfectCompleteDecisionFirst
}
