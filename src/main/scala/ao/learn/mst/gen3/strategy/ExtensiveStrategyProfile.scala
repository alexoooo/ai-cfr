package ao.learn.mst.gen3.strategy


/**
 *
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

//  def actionProbability(
//    informationSetIndex:Int,
//    actionCount:Int
//    ): Double
}
