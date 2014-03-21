package ao.learn.mst.gen5.solve2

import ao.learn.mst.gen5.state.regret.{RegretAccumulator, RegretMatcher}
import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}

/**
 *
 */
trait RegretSampler[State, InformationSet, Action]
{
  def sampleRegret(
    game              : ExtensiveGame[State, InformationSet, Action],
    abstraction       : ExtensiveAbstraction[InformationSet, Action],
    regretAccumulator : RegretAccumulator,
    player            : Int)
    : Map[Long, IndexedSeq[Double]]


  def sampleRegretAndStrategy(
    game              : ExtensiveGame[State, InformationSet, Action],
    abstraction       : ExtensiveAbstraction[InformationSet, Action],
    regretAccumulator : RegretAccumulator,
    player            : Int)
    : RegretStrategySample
}
