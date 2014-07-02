package ao.learn.mst.gen5.solve2

import ao.learn.mst.gen5.state.{MixedStrategy, OptimizationState}
import ao.learn.mst.gen5.state.regret.RegretMatcher
import ao.learn.mst.gen5.state.regret.impl.UniformDefaultRegretMatcher
import ao.learn.mst.gen5.state.impl.ArrayOptimizationState
import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}

/**
 *
 */
class RegretMinimizer[State, InformationSet, Action](
    game            : ExtensiveGame[State, InformationSet, Action],
    abstraction     : ExtensiveAbstraction[InformationSet, Action],
    state           : OptimizationState = new ArrayOptimizationState(),
    averageStrategy : Boolean = false,
    regretMatcher   : RegretMatcher = UniformDefaultRegretMatcher)
{
  //--------------------------------------------------------------------------------------------------------------------
  def strategyView: MixedStrategy =
    state.mixedStrategyView(regretMatcher, averageStrategy)


  //--------------------------------------------------------------------------------------------------------------------
  def iterate(sampler: RegretSampler[State, InformationSet, Action]): Unit = {
    for (player <- 0 until game.playerCount) {
      iteratePlayer(sampler, player)
    }
  }

  private def iteratePlayer(
      sampler: RegretSampler[State, InformationSet, Action],
      player: Int): Unit =
  {
    if (averageStrategy) {
      val strategyRegretSample: RegretStrategySample =
        sampler.sampleRegretAndStrategy(
          game, abstraction, state.regretStore, player)

      state.regretStore.commit(strategyRegretSample.regret)
      state.strategyStore.commit(strategyRegretSample.strategy)
    } else {
      val regretSample: Map[Long, Seq[Double]] =
        sampler.sampleRegret(
          game, abstraction, state.regretStore, player)

      state.regretStore.commit(regretSample)
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  def iterateAndGetMaxDelta(sampler: RegretSampler[State, InformationSet, Action]): Double = {
    var max: Double = 0
    for (player <- 0 until game.playerCount) {
      val delta = iteratePlayerAndGetMaxDelta(sampler, player)
      max = math.max(max, delta)
    }
    max
  }


  private def iteratePlayerAndGetMaxDelta(
      sampler: RegretSampler[State, InformationSet, Action],
      player: Int)
      : Double =
  {
    if (averageStrategy) {
      val strategyRegretSample: RegretStrategySample =
        sampler.sampleRegretAndStrategy(
          game, abstraction, state.regretStore, player)

      val before: Map[Long, IndexedSeq[Double]] =
        currentStrategy(strategyRegretSample.strategy)

      state.regretStore.commit(strategyRegretSample.regret)
      state.strategyStore.commit(strategyRegretSample.strategy)

      strategyMaxDelta(before, currentStrategy(strategyRegretSample.strategy))
    } else {
      val regretSample: Map[Long, IndexedSeq[Double]] =
        sampler.sampleRegret(
          game, abstraction, state.regretStore, player)

      val before: Map[Long, IndexedSeq[Double]] =
        currentStrategy(regretSample)

      state.regretStore.commit(regretSample)

      strategyMaxDelta(before, currentStrategy(regretSample))
    }
  }

  private def currentStrategy(infoActionProperties: Map[Long, Seq[_]]): Map[Long, IndexedSeq[Double]] =
    infoActionProperties.toSeq
      .map((e: (Long, Seq[_])) =>
        (e._1, strategyView.probabilities(e._1, e._2.length)))
      .toMap

  private def strategyMaxDelta(
      before: Map[Long, IndexedSeq[Double]],
      after: Map[Long, IndexedSeq[Double]]): Double =
  {
    var max: Double = 0
    for (info <- before.keys) {
      for (ba: (Double, Double) <- before(info).zip(after(info))) {
        val delta = math.abs(ba._1 - ba._2)
        max = math.max(max, delta)
      }
    }
    max
  }


  //--------------------------------------------------------------------------------------------------------------------
  def iterateToConvergence(
      sampler: RegretSampler[State, InformationSet, Action],
//      epsilon: Double = 0.000005,
      epsilon: Double = 0.001,
      horizon: Int = 19000)
      : Long =
  {
    var count = 0.toLong

    var converged = false
    while (! converged) {

      var failedToConverge = false

      var i = 0
      while (i < horizon && ! failedToConverge) {
        val delta = iterateAndGetMaxDelta(sampler)
        if (delta > epsilon) {
          failedToConverge = true
        }

        i += 1
      }

      if (! failedToConverge) {
        converged = true
      }

      count += i
    }


//    var horizonDelta = Double.NaN
//    do {
//      horizonDelta = 0
//      for (_ <- 1 to horizon) {
//        val delta = iterateAndGetDelta(sampler)
//        horizonDelta += delta
//      }
//      count += horizon
//    } while (horizonDelta > epsilon)

    count
  }
}
