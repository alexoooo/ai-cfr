
package ao.learn.mst.example.rps

import ao.learn.mst.gen2.game.ExtensiveGame
import decision.RockPaperScissorsNodeFirst


//----------------------------------------------------------------------------------------------------------------------
case object RockPaperScissorsGame
    extends ExtensiveGame
{
  def rationalPlayerCount = 2

  def treeRoot =
    RockPaperScissorsNodeFirst
}