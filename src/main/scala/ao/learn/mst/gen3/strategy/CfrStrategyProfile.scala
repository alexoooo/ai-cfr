package ao.learn.mst.gen3.strategy

import ao.learn.mst.gen.chance.ProbabilityMass

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
   * @param actionCount number of abstract actions
   * @return strategy where the probability of each action is proportional to the observed regret of not taking it
   */
  def positiveRegretStrategy(
    informationSetIndex : Int,
    actionCount         : Int
  ): ProbabilityMass
}
