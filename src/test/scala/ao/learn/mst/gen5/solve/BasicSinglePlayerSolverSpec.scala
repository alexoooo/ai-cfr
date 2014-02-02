package ao.learn.mst.gen5.solve

import ao.learn.mst.gen5.cfr.{ChanceSampledCfrMinimizer}
import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.example.monty.{BasicMontyHallGame, MontyHallGame}
import ao.learn.mst.gen5.ExtensiveGame

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
      new ChanceSampledCfrMinimizer[S, I, A]


    "Solve basic small games" in {
      def solveGame[S, I, A](game : ExtensiveGame[S, I, A], iterations : Int) : Seq[Seq[Double]] =
        SolverSpecUtils.flatSolve(game, cfrAlgorithm(), iterations)

      "Basic Monty Hall problem" in {
        val solution = solveGame(
          BasicMontyHallGame,
          4)

        val switchDecision =
          solution(1)

        switchDecision(0) must be lessThan epsilonProbability
      }

      "Monty Hall problem" in {
        val solution : Seq[Seq[Double]] = solveGame(
          MontyHallGame,
          1000)

        val initialDoorChoice : Seq[Double] =
          solution(0)

        val switchChoices : Seq[Seq[Double]] =
          solution.drop(1)

        foreach (0 until initialDoorChoice.length) {c =>
          if (initialDoorChoice(c) > epsilonProbability) {
            switchChoices(c)(0) must be lessThan epsilonProbability
          } else ok
        }
      }
    }
  }
}
