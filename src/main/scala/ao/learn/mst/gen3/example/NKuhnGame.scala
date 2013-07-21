package ao.learn.mst.gen3.example

import ao.learn.mst.gen3.game.{NExtensiveGameNode, NExtensiveGame}

/**
 * 13/07/13 8:35 PM
 */
object NKuhnGame extends NExtensiveGame[NKuhnInfoSet, NKuhnAction]
{
  def rationalPlayerCount = 2

  def treeRoot: NExtensiveGameNode[NKuhnInfoSet, NKuhnAction] =
    NKuhnGameChance
}
