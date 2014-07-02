package ao.learn.mst.gen5.state.regret

/**
 *
 */
trait RegretMatcher
{
  /**
   * Compute action probabilities as the proportion of positive counterfactual regret of not taking each action.
   * If there is no positive regret for any action, than an arbitrary (e.g. uniform) distribution is used.
   *
   * @param cumulativeRegret known action regret sum (possibly empty)
   * @param actionCount the number of abstract actions at the given information set
   * @return positive regret matching strategy
   */
  def positiveRegretStrategy(cumulativeRegret: Seq[Double], actionCount: Int): IndexedSeq[Double]
}
