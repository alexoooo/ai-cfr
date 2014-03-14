package ao.learn.mst.gen5.solve2

import ao.learn.mst.gen5.state.regret.RegretMatcher

/**
 *
 */
trait RegretSampler[State, InformationSet, Action]
{
  def sampleRegret(
    abstractGame : AbstractGame[State, InformationSet, Action],
    player       : Int
    ): Map[Long, Seq[Double]]


  def sampleStrategyAndRegret(
    abstractGame : AbstractGame[State, InformationSet, Action],
    player       : Int
    ): StrategyRegretSample
}
