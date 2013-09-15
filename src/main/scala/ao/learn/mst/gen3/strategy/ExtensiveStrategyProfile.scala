package ao.learn.mst.gen3.strategy


/**
 * 20/06/13 9:35 PM
 */
trait ExtensiveStrategyProfile
{
  def actionProbabilityMass(
    informationSetIndex:Int,
    actionCount:Int
    ): Seq[Double]

//  def actionProbability(
//    informationSetIndex:Int,
//    actionCount:Int
//    ): Double
}
