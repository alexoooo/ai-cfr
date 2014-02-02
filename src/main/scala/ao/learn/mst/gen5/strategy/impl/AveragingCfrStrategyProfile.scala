package ao.learn.mst.gen5.strategy.impl

import ao.learn.mst.gen5.strategy.{CfrStrategyProfile, CfrRegretConsumer, ExtensiveStrategyProfile, CfrStrategyBuilder}



class AveragingCfrStrategyProfile(
    baseProfile : CfrStrategyProfile)
  extends CfrStrategyProfile
{
  //--------------------------------------------------------------------------------------------------------------------
  private val average =
    new ArrayCfrAverageStrategyBuilder


  //--------------------------------------------------------------------------------------------------------------------
  def positiveRegretMatchingStrategy(informationSetIndex: Int): Seq[Double] =
    baseProfile.positiveRegretMatchingStrategy(informationSetIndex)

  def positiveRegretMatchingStrategy(informationSetIndex: Int, actionCount: Int): Seq[Double] =
    baseProfile.positiveRegretMatchingStrategy(informationSetIndex, actionCount)

  def toExtensiveStrategyProfile: ExtensiveStrategyProfile =
    average.toExtensiveStrategyProfile


  //--------------------------------------------------------------------------------------------------------------------
  def update(informationSetIndex: Int, actionRegret: Seq[Double], opponentReachProbability: Double) {
    val actionCount:Int =
      actionRegret.length

    val strategy: Seq[Double] =
      positiveRegretMatchingStrategy(informationSetIndex, actionCount)

    baseProfile.update(informationSetIndex, actionRegret, opponentReachProbability)

    average.update(informationSetIndex, strategy, opponentReachProbability)
  }


  def update(informationSetIndex: Int, counterfactualRegret: Seq[Double]): Unit =
    ???
}
