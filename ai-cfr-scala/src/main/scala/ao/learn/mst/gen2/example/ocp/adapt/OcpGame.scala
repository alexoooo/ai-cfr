package ao.learn.mst.gen2.example.ocp.adapt

import ao.learn.mst.gen2.game.ExtensiveGame


/**
 * Copied from (in case link goes down):
 *  http://pokerai.org/pf3/viewtopic.php?f=3&t=3991&hilit=
 *
 *    This is what I found out with my results:

      One Card Poker has only one complete unique NE-strategy. And this is for P2 after P1 has passed to him:
      Bet always with 2-3-9-T-J-Q-K-A
      Check always with 4-5-6-7-8

      For other nodes there are only partially unique NE-strategys which never change:
      P1 First round: Check always with 5-6-7-8
      P1 Second round: Fold always with 2-3 and Call always with 9 to Ace
      P2 on bet: Fold always with 2-3-4 and Call always with 9 to Ace

      All other nodes have inifite equilibrium strategys which differ every computation.
      - By: TheFell
 *
 * CfrMinimizer applied to OcpGame produces identical strategy where applicable to the one described above.
 * Also, for the other information sets, the solution was very similar to:
 *  http://www.cs.cmu.edu/~ggordon/poker/
 *
 * Probabilities for 1st player:
 *  card	after-Check/Raise	FirstAction
    2	0.022	48.403
    3	0.021	47.244
    4	0.019	29.129
    5	35.235	0.2
    6	44.893	0.188
    7	74.019	0.215
    8	83.468	0.399
    9	99.978	51.855
    10	99.978	62.861
    11	99.978	62.861
    12	99.978	62.861
    13	99.978	62.861
    14	99.978	62.861
 *
 * Probabilities for second player:
 *  card	after-Check	after-Raise
    2	99.842	0.034
    3	89.563	0.034
    4	5.192	0.032
    5	0.08	45.782
    6	0.083	49.052
    7	0.105	52.054
    8	0.265	56.108
    9	99.139	99.966
    10	99.981	99.965
    11	99.981	99.965
    12	99.981	99.965
    13	99.981	99.965
    14	99.981	99.965
 *
 */
object OcpGame
  extends ExtensiveGame
{
  //--------------------------------------------------------------------------------------------------------------------
  def rationalPlayerCount = 2

  /**"A rooted tree, called the game tree" */
  def treeRoot = OcpGameChance
}