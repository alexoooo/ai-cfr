package ao.learn.mst.gen.utility

/**
 * "Each terminal (leaf) node of the game tree has an n-tuple of payoffs,
 *  meaning there is one payoff for each player at the end of every possible play"
 */
case class ExtensivePayoff(
    playerPayoffs : Seq[Double])