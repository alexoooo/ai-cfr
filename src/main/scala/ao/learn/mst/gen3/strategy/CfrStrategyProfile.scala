package ao.learn.mst.gen3.strategy


//----------------------------------------------------------------------------------------------------------------------
trait CfrStrategyProfile
{
  //--------------------------------------------------------------------------------------------------------------------
  def toExtensiveStrategyProfile: ExtensiveStrategyProfile


  //--------------------------------------------------------------------------------------------------------------------
  def update(
    informationSetIndex      : Int,
    actionRegret             : Seq[Double],
    opponentReachProbability : Double)


  //--------------------------------------------------------------------------------------------------------------------
  /**
   * @param informationSetIndex abstract game tree node
   * @return strategy where the probability of each action is proportional to the observed regret of not taking it
   *         if the given information set is not recognized, an empty sequence is returned.
   */
  def positiveRegretStrategy(
    informationSetIndex: Int
    ): Seq[Double]

  /**
   * @param informationSetIndex abstract game tree node
   * @param actionCount number of abstract actions
   * @return strategy where the probability of each action is proportional to the observed regret of not taking it
   *         if the information set is not recognized, all actions are given equal probability.
   */
  def positiveRegretStrategy(
    informationSetIndex : Int,
    actionCount         : Int
    ): Seq[Double]
}
