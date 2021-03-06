package ao.learn.mst.gen2.game


/**
 * http://en.wikipedia.org/wiki/Extensive-form_game
 * "Following the presentation from Hart (1992), an n-player extensive-form game thus consists of the following:"
 *
 *
 * http://poker.cs.ualberta.ca/publications/abourisk.msc.pdf
 *  2.1.1 Definitions -> Definition 1 [19, p. 200]
 *    a finite extensive game with imperfect information has the following components...
 */
trait ExtensiveGame
{
  //--------------------------------------------------------------------------------------------------------------------
  /** "A finite set of n (rational) players" */
  //def rationalPlayers : Set[RationalPlayer]
  def rationalPlayerCount : Int

//  /** Utility range for each (rational) player */
//  def utilityRanges : Map[RationalPlayer, ExtensiveUtilityRange]

  /** "A rooted tree, called the game tree" */
  def treeRoot : ExtensiveGameNode
}