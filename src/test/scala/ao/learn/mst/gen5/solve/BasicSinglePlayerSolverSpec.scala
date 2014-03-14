package ao.learn.mst.gen5.solve

import ao.learn.mst.gen5.cfr.{OutcomeSampling2CfrMinimizer, ProbingCfrMinimizer, OutcomeSamplingCfrMinimizer, ChanceSampledCfrMinimizer}
import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.example.monty.{BasicMontyHallGame, MontyHallGame}
import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.state.MixedStrategy

/**
 *
 */
class BasicSinglePlayerSolverSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  val epsilonProbability:Double =
    0.01


  //--------------------------------------------------------------------------------------------------------------------
  "Counterfactual Regret Minimization algorithm" should
  {
    def cfrAlgorithm[S, I, A]() : ExtensiveSolver[S, I, A] =
//      new ChanceSampledCfrMinimizer[S, I, A]
//      new OutcomeSamplingCfrMinimizer[S, I, A]
      new OutcomeSampling2CfrMinimizer[S, I, A]
//      new ProbingCfrMinimizer[S, I, A]


    "Solve basic small games" in {
      def solveGame[S, I, A](game: ExtensiveGame[S, I, A], iterations: Int): MixedStrategy =
        SolverSpecUtils.solve(game, cfrAlgorithm(), iterations)

      "Basic Monty Hall problem" in {
        val solution = solveGame(
          BasicMontyHallGame,
          200)

        val switchDecision =
          solution.probabilities(1, 2)

        switchDecision(0) must be lessThan epsilonProbability
      }

      "Monty Hall problem" in {
        val solution: MixedStrategy = solveGame(
          MontyHallGame,
          1000)

        val initialDoorChoice: Seq[Double] =
          solution.probabilities(0, 3)

        val switchChoices: Seq[Seq[Double]] =
          (1 to 6).map(solution.probabilities(_, 2))

        foreach (0 until initialDoorChoice.length) {c =>
          if (initialDoorChoice(c) > epsilonProbability) {
            switchChoices(c)(0) must be lessThan epsilonProbability
          } else ok
        }
      }
    }
  }
}
