package ao.learn.mst.gen5.strategy.impl

import ao.learn.mst.gen5.strategy.ExtensiveStrategyProfile


object ObliviousExtensiveStrategyProfile extends ExtensiveStrategyProfile
{
  def size: Long = 0

  def actionProbabilityMass(informationSetIndex: Long, actionCount: Int): Seq[Double] = {
    val equalProbability : Double =
      1.0 / actionCount

    Seq.fill(actionCount)(equalProbability)
  }

  def actionProbabilityMass(informationSetIndex: Long): Seq[Double] =
    Seq.empty
}
