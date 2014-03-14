package ao.learn.mst.gen5.state.impl

import ao.learn.mst.gen5.state.OptimizationState
import ao.learn.mst.gen5.state.strategy.StrategyStore
import ao.learn.mst.gen5.state.strategy.impl.ArrayStrategyStore
import ao.learn.mst.gen5.state.regret.RegretStore
import ao.learn.mst.gen5.state.regret.impl.ArrayRegretStore
import java.io.File

/**
 *
 */
class ArrayOptimizationState
    extends OptimizationState
{
  val strategyStore =
    new ArrayStrategyStore

  val regretStore =
    new ArrayRegretStore


  def read(directoryPath: String): Unit = {
    strategyStore.read(new File(directoryPath, ArrayOptimizationState.strategyStoreFile))
    regretStore.read(new File(directoryPath, ArrayOptimizationState.regretStoreFile))
  }

  def write(directoryPath: String): Unit = {
    strategyStore.write(new File(directoryPath, ArrayOptimizationState.strategyStoreFile))
    regretStore.write(new File(directoryPath, ArrayOptimizationState.regretStoreFile))
  }
}


object ArrayOptimizationState
{
  private val strategyStoreFile = "strategy.bin"
  private val regretStoreFile = "regret.bin"
}