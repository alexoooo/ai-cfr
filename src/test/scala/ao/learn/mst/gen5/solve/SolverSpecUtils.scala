package ao.learn.mst.gen5.solve

import ao.learn.mst.gen5.node.{Decision, ExtensiveNode}
import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.learn.mst.gen5.example.abstraction.{LosslessInfoLosslessDecisionAbstractionBuilder, OpaqueAbstractionBuilder}
import ao.learn.mst.gen5.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}


object SolverSpecUtils
{
  def flatSolve[S, I, A](
      game       : ExtensiveGame[S, I, A],
      solver     : ExtensiveSolver[S, I, A],
      iterations : Int)
    : Seq[Seq[Double]] =
  {
    val strategy : ExtensiveStrategyProfile =
      SolverSpecUtils.solve(game, solver, iterations)

    (0 until strategy.knownInformationSetCount)
      .map(strategy.actionProbabilityMass)
  }

  def solve[S, I, A](
      game       : ExtensiveGame[S, I, A],
      solver     : ExtensiveSolver[S, I, A],
      iterations : Int)
    : ExtensiveStrategyProfile =
  {
    val solution : SolutionApproximation[I, A] =
      solver.initialSolution(game)

    val abstractionBuilder : OpaqueAbstractionBuilder =
      LosslessInfoLosslessDecisionAbstractionBuilder

    val abstraction : ExtensiveAbstraction[I, A] =
      abstractionBuilder.generate(game)

    for (i <- 1 to iterations) {
      solution.optimize(abstraction)
    }

    val strategy : ExtensiveStrategyProfile =
      solution.strategy

    strategy
  }

  def getDecisionInformationSet[I, A](
    decisionNode : ExtensiveNode[I, A]) : I =
  {
    decisionNode match {
      case Decision(_, infoSet, _) =>
        infoSet

      case _ => throw new IllegalArgumentException
    }
  }
}


