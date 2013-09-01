package ao.learn.mst.gen5.solve

import ao.learn.mst.gen5.ExtensiveGame


trait ExtensiveSolver[State, InformationSet, Action]
{
  def initiateSolution(
    game : ExtensiveGame[State, InformationSet, Action]
    ) : SolutionApproximation[InformationSet, Action]
}
