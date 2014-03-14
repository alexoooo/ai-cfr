package ao.learn.mst.gen5.solve2

import ao.learn.mst.gen5.state.{MixedStrategy, OptimizationState}
import ao.learn.mst.gen5.state.regret.RegretMatcher
import ao.learn.mst.gen5.state.regret.impl.UniformDefaultRegretMatcher
import ao.learn.mst.gen5.state.impl.ArrayOptimizationState

/**
 *
 */
case class RegretMinimizer[State, InformationSet, Action](
    abstractGame: AbstractGame[State, InformationSet, Action],
    state: OptimizationState = new ArrayOptimizationState(),
    averageStrategy: Boolean = false,
    regretMatcher: RegretMatcher = UniformDefaultRegretMatcher)
{
//  private val regretBuffer: RegretBuffer =
//    new MapRegretBuffer
//
//  private val strategyBuffer: StrategyBuffer =
//    new MapStrategyBuffer


  def strategyView: MixedStrategy =
    state.mixedStrategyView(regretMatcher, averageStrategy)


  // todo: return Double strategy delta?
  def iterate(sampler: RegretSampler[State, InformationSet, Action]): Unit = {
    for (player <- 0 until abstractGame.game.playerCount) {
      iteratePlayer(sampler, player)
    }
  }

  private def iteratePlayer(
      sampler: RegretSampler[State, InformationSet, Action],
      player: Int): Unit =
  {
    if (averageStrategy) {
      val strategyRegretSample: StrategyRegretSample =
        sampler.sampleStrategyAndRegret(
          abstractGame, player)

      state.regretStore.commit(strategyRegretSample.regret)
      state.strategyStore.commit(strategyRegretSample.strategy)

//      strategyRegretSample.regret
    } else {
      val regretSample: Map[Long, Seq[Double]] =
        sampler.sampleRegret(
          abstractGame, player)

      state.regretStore.commit(regretSample)

//      regretSample
    }
  }

//  private def regretDelta(regretSample: Map[Long, Seq[Double]]): Double = {
//    for ((info, regret) <- regretSample) {
//
//    }
//  }
//
//  private def absoluteRegretDelta(info: Long, regret: Seq[Double]): Double = {
//    val existingRegret: Seq[Double] =
//      state.regretStore.cumulativeRegret(info)
//
//    require(existingRegret.length <= regret.length)
//
//    regretMatcher.positiveRegretStrategy(existingRegret)
//
//    existingRegret
//      .zipAll(regret, 0, 0)
//      .map(case (existing, additional) => existing)
//
//
//    for (action <- 0 until regret.length) {
//
//    }
//
//  }




//  def iterateToConvergence(
//      sampler: RegretSampler[State, InformationSet, Action],
//      epsilon: Double
//      ): Long =
//  {
//    ???
//  }
}
