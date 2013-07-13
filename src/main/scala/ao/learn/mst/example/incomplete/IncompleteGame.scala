package ao.learn.mst.example.incomplete

import ao.learn.mst.gen2.game.ExtensiveGame
import node.IncompleteChance


//----------------------------------------------------------------------------------------------------------------------
/**
 * Game tree for first game at:
 *  http://en.wikipedia.org/wiki/Extensive-form_game#Incomplete_information
 *
 * Optimal play:
 * - player 1:
 *  - type 1 plays D
 *  - type 2 plays U
 * - player 2:
 *  - if observes U then randomises
 *  - if observes D then plays U'.
 */
object IncompleteGame
    extends ExtensiveGame
{
  //--------------------------------------------------------------------------------------------------------------------
  def rationalPlayerCount = 2

  def treeRoot =
    IncompleteChance
}
