package ao.learn.mst.gen3.strategy


//----------------------------------------------------------------------------------------------------------------------
/**
 *
 */
trait CfrRegretStrategy
{
  //--------------------------------------------------------------------------------------------------------------------
  /**
   * @param informationSetIndex abstract game tree node
   * @return strategy where the probability of each action is proportional to the observed regret of not taking it
   *         if the given information set is not recognized, an empty sequence is returned.
   */
  def positiveRegretMatchingStrategy(
    informationSetIndex: Int
    ): Seq[Double]

  /**
   * @param informationSetIndex abstract game tree node
   * @param abstractActionCount number of abstract actions
   * @return strategy where the probability of each action is proportional to the observed regret of not taking it
   *         if the information set is not recognized, all actions are given equal probability.
   */
  def positiveRegretMatchingStrategy(
    informationSetIndex : Int,
    abstractActionCount : Int
    ): Seq[Double]
}
