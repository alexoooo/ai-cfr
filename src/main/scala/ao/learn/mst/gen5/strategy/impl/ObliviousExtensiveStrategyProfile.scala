package ao.learn.mst.gen5.strategy.impl

import ao.learn.mst.gen5.strategy.ExtensiveStrategyProfile


object ObliviousExtensiveStrategyProfile extends ExtensiveStrategyProfile
{
  def knownInformationSetCount: Int = 0

  def actionProbabilityMass(informationSetIndex: Int, actionCount: Int): Seq[Double] = {
    val equalProbability : Double =
      1.0 / actionCount

    Seq.fill(actionCount)(equalProbability)
  }

  def actionProbabilityMass(informationSetIndex: Int): Seq[Double] =
    Seq.empty
}