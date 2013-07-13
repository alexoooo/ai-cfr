package ao.learn.mst.gen3.game


//----------------------------------------------------------------------------------------------------------------------
/**
 * "Each terminal (leaf) node of the game tree has an n-tuple of payoffs,
 *  meaning there is one payoff for each player at the end of every possible play".
 *
 * @param outcomes payoffs
 */
case class NExpectedValue(outcomes : Seq[Double]) {
//  def apply(index: Int) =
//    outcomes(index)
//
//  def apply(player: RationalPlayer) =
//    apply(player.index)
}