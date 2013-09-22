package ao.learn.mst.gen3.strategy.impl

import ao.learn.mst.gen3.strategy.{CfrStrategyProfile, CfrRegretConsumer, ExtensiveStrategyProfile, CfrStrategyBuilder}



class AveragingCfrStrategyProfile(
    baseProfile : CfrStrategyProfile)
  extends CfrStrategyProfile
{
  //--------------------------------------------------------------------------------------------------------------------
  private val average =
    new ArrayCfrAverageStrategyBuilder


  //--------------------------------------------------------------------------------------------------------------------
  def positiveRegretStrategy(informationSetIndex: Int): Seq[Double] =
    baseProfile.positiveRegretStrategy(informationSetIndex)

  def positiveRegretStrategy(informationSetIndex: Int, actionCount: Int): Seq[Double] =
    baseProfile.positiveRegretStrategy(informationSetIndex, actionCount)

  def toExtensiveStrategyProfile: ExtensiveStrategyProfile =
    average.toExtensiveStrategyProfile


  //--------------------------------------------------------------------------------------------------------------------
  def update(informationSetIndex: Int, actionRegret: Seq[Double], opponentReachProbability: Double) {
    val actionCount = actionRegret.length

    val strategy : Seq[Double] =
      positiveRegretStrategy(informationSetIndex, actionCount)

    baseProfile.update(informationSetIndex, actionRegret, opponentReachProbability)
    average.update(informationSetIndex, strategy, opponentReachProbability)
  }
}
