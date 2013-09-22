package ao.learn.mst.gen5

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.solve.ExtensiveSolver
import ao.learn.mst.gen5.cfr.ChanceSampledCfrMinimizer
import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.example.matrix.MatrixGames

/**
 * http://en.wikipedia.org/wiki/Normal_form_game
 */
class BasicMatrixSolverSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  val epsilonProbability:Double =
    0.01


  //--------------------------------------------------------------------------------------------------------------------
  "Counterfactual Regret Minimization algorithm" should
  {
    "Solve normal-form matrix games" in {
      def solveNormalFormGame[S, I, A](
          game             : ExtensiveGame[S, I, A],
          iterations       : Int,
          actionCounts     : Option[(Int, Int)] = None,
          twoPlayerZeroSum : Boolean = false): (Seq[Double], Seq[Double]) =
      {
        val solver : ExtensiveSolver[S, I, A] =
          new ChanceSampledCfrMinimizer[S, I, A](twoPlayerZeroSum)

        val strategy : ExtensiveStrategyProfile =
          SolverSpecUtils.solve(game, solver, iterations)

        val rowActionProbabilities : Seq[Double] =
          actionCounts match {
            case None    => strategy.actionProbabilityMass(0)
            case Some(c) => strategy.actionProbabilityMass(0, c._1)
          }

        val columnActionProbabilities : Seq[Double] =
          actionCounts match {
            case None    => strategy.actionProbabilityMass(1)
            case Some(c) => strategy.actionProbabilityMass(1, c._2)
          }

        (rowActionProbabilities, columnActionProbabilities)
      }

      "Classic 2x2 games" in {
        "Matching Pennies" in {
          val (row, col) = solveNormalFormGame(
            MatrixGames.matchingPennies,
            1, Some((2, 2)))

          row.max should be lessThan 0.5 + epsilonProbability
          col.max should be lessThan 0.5 + epsilonProbability
        }

        "Deadlock" in {
          val (row, col) = solveNormalFormGame(
            MatrixGames.deadlock,
            60)

          row(0) should be lessThan epsilonProbability
          col(0) should be lessThan epsilonProbability
        }

        "Prisoner's Dilemma" in {
          val (row, col) = solveNormalFormGame(
            MatrixGames.prisonersDilemma,
            60)

          row(0) should be lessThan epsilonProbability
          col(0) should be lessThan epsilonProbability
        }

        "Battle of the Sexes" in {
          val (row, col) = solveNormalFormGame(
            MatrixGames.battleOfTheSexes,
            5 * 1000)

          if (row(0) < 0.3) {
            row(0) should be lessThan epsilonProbability
            col(0) should be lessThan epsilonProbability
          } else if (row(1) < 0.3) {
            row(1) should be lessThan epsilonProbability
            col(1) should be lessThan epsilonProbability
          } else {
            // what would the optimal strategy be?
            ???
//            row.max should be lessThan 0.5 + epsilonProbability
//            col.max should be lessThan 0.5 + epsilonProbability
          }
        }
      }

      "Other matrix games" in {
        "Zero Sum" in {
          val (row, col) = solveNormalFormGame(
            MatrixGames.zeroSum,
            4 * 1000, None, twoPlayerZeroSum = true)

          row(0) must be greaterThan(4.0/7 - epsilonProbability)
          row(1) must be greaterThan(3.0/7 - epsilonProbability)

          col(0) should be lessThan epsilonProbability
          col(1) must be greaterThan(4.0/7 - epsilonProbability)
          col(2) must be greaterThan(3.0/7 - epsilonProbability)
        }
      }
    }
  }
}
