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


  override def equals(other: Any): Boolean =
    other match {
      case that: ArrayOptimizationState =>
        strategyStore == that.strategyStore &&
        regretStore == that.regretStore

      case _ => false
    }
}


object ArrayOptimizationState
{
  private val strategyStoreFile = "strategy.bin"
  private val regretStoreFile = "regret.bin"


  def readOrEmpty(directoryPath: String): ArrayOptimizationState = {
    val instance = new ArrayOptimizationState

    if (new File(directoryPath).exists()) {
      instance.read(directoryPath)
    }

    instance
  }
}