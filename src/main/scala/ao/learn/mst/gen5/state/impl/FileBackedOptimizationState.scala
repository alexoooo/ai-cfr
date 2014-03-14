package ao.learn.mst.gen5.state.impl

import ao.learn.mst.gen5.state.OptimizationState
import java.io.{File, Closeable}
import ao.learn.mst.gen5.state.strategy.StrategyStore
import ao.learn.mst.gen5.state.regret.RegretStore

/**
 *
 */
class FileBackedOptimizationState(directoryPath: File)
    extends OptimizationState
    with Closeable
{
  override def strategyStore: StrategyStore = ???

  override def regretStore: RegretStore = ???

  override def close(): Unit = ???
}
