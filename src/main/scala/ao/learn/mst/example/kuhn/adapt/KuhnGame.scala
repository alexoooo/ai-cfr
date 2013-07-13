package ao.learn.mst.example.kuhn.adapt

import ao.learn.mst.gen2.game.ExtensiveGame


/**
 *
 */
object KuhnGame
    extends ExtensiveGame
{
  //--------------------------------------------------------------------------------------------------------------------
//  def rationalPlayers =
//    Set(RationalPlayer(0),
//        RationalPlayer(1))
//
////  def utilityRanges = {
////    val utilityRange =
////      ExtensiveUtilityRange(-2.0, 2.0)
////
////    Seq.fill(2)(utilityRange)
////  }
//
//  def gameTreeRoot = null

  def rationalPlayerCount = 2

  /**"A rooted tree, called the game tree" */
  def treeRoot = KuhnGameChance
}