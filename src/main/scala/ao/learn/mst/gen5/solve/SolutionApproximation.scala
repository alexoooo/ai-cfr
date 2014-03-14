package ao.learn.mst.gen5.solve

import ao.learn.mst.gen5.ExtensiveAbstraction
import ao.learn.mst.gen5.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.state.MixedStrategy


trait SolutionApproximation[InformationSet, Action]
{
  def optimize(extensiveAbstraction: ExtensiveAbstraction[InformationSet, Action]): Unit
  
//  def clear(informationSetIndex : Int)

//  def strategy: ExtensiveStrategyProfile

  def strategyView: MixedStrategy
}
