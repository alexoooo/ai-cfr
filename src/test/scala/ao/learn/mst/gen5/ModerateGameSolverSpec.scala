package ao.learn.mst.gen5

import ao.learn.mst.gen5.solve.ExtensiveSolver
import ao.learn.mst.gen5.cfr.ChanceSampledCfrMinimizer
import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.example.monty.MontyHallGame

/**
 * 22/09/13 7:59 PM
 */
class ModerateGameSolverSpec
//  extends SpecificationWithJUnit
{
//  //--------------------------------------------------------------------------------------------------------------------
//  val epsilonProbability:Double =
//    0.01
//
//
//  //--------------------------------------------------------------------------------------------------------------------
//  "Counterfactual Regret Minimization algorithm" should
//  {
//      def cfrAlgorithm[S, I, A]() : ExtensiveSolver[S, I, A] =
//        new ChanceSampledCfrMinimizer[S, I, A]
//
//    "Solve moderate complexity games" in {
//      def solveGame[S, I, A](game : ExtensiveGame[S, I, A], iterations : Int) : Seq[Seq[Double]] =
//        SolverSpecUtils.flatSolve(game, cfrAlgorithm(), iterations)
//
//      "Monty Hall problem" in {
//        val solution : Seq[Seq[Double]] = solveGame(
//          MontyHallGame,
//          10 * 1000 * 1000)
//
//        val switchChoices =
//          solution.drop(1)
//
//        foreach (switchChoices) {c =>
//          c(0) must be lessThan epsilonProbability
//        }
//      }
//    }
//  }
}
