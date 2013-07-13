package ao.learn.mst.example.zerosum

import ao.learn.mst.gen2.game.ExtensiveGame
import node.ZeroSumDecisionRed

/**
 * http://en.wikipedia.org/wiki/Zero%E2%80%93sum_game
 * Quote:
 *  Red should choose action 1 with probability 4/7 and action 2 with probability 3/7,
 *  while Blue should assign the probabilities 0, 4/7, and 3/7 to the three actions A, B, and C.
 *  Red will then win 20/7 points on average per game.
 *
 * Red: 57.143% / 42.857%
 * Blue: 0 / 57.143% / 42.857%
 */
object ZeroSumGame
  extends ExtensiveGame
{
  def rationalPlayerCount = 2

  def treeRoot = ZeroSumDecisionRed
}
