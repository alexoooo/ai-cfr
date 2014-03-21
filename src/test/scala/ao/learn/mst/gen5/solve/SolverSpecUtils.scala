package ao.learn.mst.gen5.solve

import ao.learn.mst.gen5.node.{Decision, ExtensiveNode}
import ao.learn.mst.gen5.example.abstraction.OpaqueAbstractionBuilder
import ao.learn.mst.gen5.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}
import ao.learn.mst.gen5.state.MixedStrategy
import ao.learn.mst.gen5.abstraction.LosslessInfoLosslessDecisionAbstractionBuilder
import ao.learn.mst.gen5.solve2.{RegretSampler, RegretMinimizer}


object SolverSpecUtils
{
//  def flatSolve[S, I, A](
//      game       : ExtensiveGame[S, I, A],
//      solver     : ExtensiveSolver[S, I, A],
//      iterations : Int
//      ): Seq[Seq[Double]] =
//  {
//    val strategy: MixedStrategy =
//      SolverSpecUtils.solve(game, solver, iterations)
//
//    (0 until strategy.size.toInt)
//      .map(strategy.actionProbabilityMass)
//  }

  def solveWithSummary[S, I, A](
      game: ExtensiveGame[S, I, A],
      solver: RegretSampler[S, I, A],
      averageStrategy: Boolean = false)
      : MixedStrategy =
  {
    val minimizer = new RegretMinimizer(game,
      LosslessInfoLosslessDecisionAbstractionBuilder.generate(game),
      averageStrategy = averageStrategy)

    val iterations =
      minimizer.iterateToConvergence(solver)

    print(s"> $game converged in $iterations iterations\n\n")

    minimizer.strategyView
  }



  def solve[S, I, A](
      game       : ExtensiveGame[S, I, A],
      solver     : ExtensiveSolver[S, I, A],
      iterations : Int
      ): MixedStrategy =
  {
    solveWithAbstraction(game, solver, iterations)._1
  }

  def solveWithAbstraction[S, I, A](
      game       : ExtensiveGame[S, I, A],
      solver     : ExtensiveSolver[S, I, A],
      iterations : Int
      ): (MixedStrategy, ExtensiveAbstraction[I, A]) =
  {
    val solution: SolutionApproximation[I, A] =
      solver.initialSolution(game)

    val abstractionBuilder: OpaqueAbstractionBuilder =
      LosslessInfoLosslessDecisionAbstractionBuilder

    val abstraction: ExtensiveAbstraction[I, A] =
      abstractionBuilder.generate(game)

    for (i <- 1 to iterations) {
      solution.optimize(abstraction)
    }

    val strategy : MixedStrategy =
      solution.strategyView

    (strategy, abstraction)
  }


  def getDecisionInformationSet[I, A](
      decisionNode: ExtensiveNode[I, A]): I =
  {
    decisionNode match {
      case Decision(_, infoSet, _) =>
        infoSet

      case _ => throw new IllegalArgumentException
    }
  }
}


