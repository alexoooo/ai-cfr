package ao.learn.mst.gen3.strategy


/**
 * Mixed strategy with probabilities of performing each action for every information set.
 */
trait ExtensiveStrategyProfile
{
  def knownInformationSetCount: Int

  def actionProbabilityMass(
    informationSetIndex:Int) : Seq[Double]

  def actionProbabilityMass(
    informationSetIndex:Int,
    actionCount:Int
    ): Seq[Double]
}
