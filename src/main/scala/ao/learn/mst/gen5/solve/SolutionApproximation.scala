package ao.learn.mst.gen5.solve

import ao.learn.mst.gen5.{ExtensiveAbstraction}

trait SolutionApproximation[InformationSet, Action]
{
  def optimize(extensiveAbstraction : ExtensiveAbstraction[InformationSet, Action])
  
//  def clear(informationSetIndex : Int)

  def strategy : MixedStrategy
}
