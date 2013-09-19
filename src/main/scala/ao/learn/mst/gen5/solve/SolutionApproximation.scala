package ao.learn.mst.gen5.solve

import ao.learn.mst.gen5.ExtensiveAbstraction
import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile


trait SolutionApproximation[InformationSet, Action]
{
  def optimize(extensiveAbstraction : ExtensiveAbstraction[InformationSet, Action]) : Unit
  
//  def clear(informationSetIndex : Int)

  def strategy : ExtensiveStrategyProfile
}
