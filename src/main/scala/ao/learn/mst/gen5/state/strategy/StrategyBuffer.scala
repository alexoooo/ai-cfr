package ao.learn.mst.gen5.state.strategy

/**
 *
 */
trait StrategyBuffer
    extends StrategyAccumulator
{
  def add(
    informationSetIndex: Long,
    currentPositiveRegretStrategy: Seq[Double],
    externalReachProbability: Double): Unit


  def flush(regretStore: StrategyStore): Unit
}
