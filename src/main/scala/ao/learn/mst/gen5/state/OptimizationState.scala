package ao.learn.mst.gen5.state

import ao.learn.mst.gen5.state.regret.{RegretMatcher, RegretStore}
import ao.learn.mst.gen5.state.strategy.StrategyStore
import ao.learn.mst.gen5.state.impl.MixedStrategyView
import scalaz._
import Scalaz._

/**
 *
 */
trait OptimizationState
{
  def strategyStore: StrategyStore
  def regretStore: RegretStore

  def mixedStrategyView(
      regretMatcher: RegretMatcher,
      averaging: Boolean)
      : MixedStrategyView =
  {
    val optionalStrategyStore: Option[StrategyStore] =
      averaging.option(strategyStore)

    new MixedStrategyView(regretMatcher, regretStore, optionalStrategyStore)
  }
}
