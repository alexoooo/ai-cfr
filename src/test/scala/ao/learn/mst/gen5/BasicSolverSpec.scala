package ao.learn.mst.gen5

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.learn.mst.gen5.cfr.ChanceSampledCfrMinimizer
import ao.learn.mst.gen5.example.abstraction.{SingleStateLosslessDecisionAbstractionBuilder, OpaqueAbstractionBuilder}
import ao.learn.mst.gen5.node.Decision
import ao.learn.mst.gen.chance.ProbabilityMass
import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.example.simple.DeterministicBinaryBanditGame


class BasicSolverSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  val epsilonProbability:Double =
      0.01
//    0.05


  //--------------------------------------------------------------------------------------------------------------------
  "Counterfactual Regret Minimization algorithm" should
  {
    def cfrAlgorithm[S, I, A]() : ExtensiveSolver[S, I, A] =
      new ChanceSampledCfrMinimizer[S, I, A]


    "Solve singleton information set games" in {
      def solveSingletonInformationSetGame[S, I, A](
          game       : ExtensiveGame[S, I, A],
          iterations : Int): Seq[Double] =
      {
        val solver : ExtensiveSolver[S, I, A] =
          cfrAlgorithm()

        val solution : SolutionApproximation[I, A] =
          solver.initialSolution(game)

        val abstractionBuilder : OpaqueAbstractionBuilder =
          new SingleStateLosslessDecisionAbstractionBuilder

        val abstraction : ExtensiveAbstraction[I, A] =
          abstractionBuilder.generate(game)

        for (i <- 1 to iterations) {
          solution.optimize(abstraction)
        }

        val strategy : ExtensiveStrategyProfile =
          solution.strategy

        val informationSet : I =
          game.node(game.initialState) match {
            case Decision(_, infoSet, _) => infoSet
            case n => throw new Error(s"Unexpected node: $n")
          }

        val actionCount : Int =
          abstraction.actionCount(informationSet)

        val actionProbabilities : ProbabilityMass =
          strategy.actionProbabilityMass(0, actionCount)

        actionProbabilities.probabilities
      }

      "Classical bandit setting" in {
        "Deterministic Binary Bandit" in {
          val optimalStrategy = solveSingletonInformationSetGame(
            DeterministicBinaryBanditGame, 64)

          optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
        }
      }
    }
  }
}
