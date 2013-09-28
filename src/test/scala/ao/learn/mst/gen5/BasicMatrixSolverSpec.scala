package ao.learn.mst.gen5

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.solve.ExtensiveSolver
import ao.learn.mst.gen5.cfr.ChanceSampledCfrMinimizer
import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.example.matrix.MatrixGames
import scala._
import ao.learn.mst.gen5.cfr.ChanceSampledCfrMinimizer
import org.specs2.matcher.{MatchSuccess, Expectable, Matcher, MatchResult}

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
          game       : ExtensiveGame[S, I, A],
          iterations : Int,
          zeroSum    : Boolean = false): (Seq[Double], Seq[Double]) =
      {
        val solver : ExtensiveSolver[S, I, A] =
          new ChanceSampledCfrMinimizer[S, I, A](zeroSum)

        val strategy : ExtensiveStrategyProfile =
          SolverSpecUtils.solve(game, solver, iterations)

        val rowActionProbabilities : Seq[Double] =
          strategy.actionProbabilityMass(0)

        val columnActionProbabilities : Seq[Double] =
          strategy.actionProbabilityMass(1)

        (rowActionProbabilities, columnActionProbabilities)
      }

      "Classic 2x2 games" in {
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

        "Matching Pennies" in {
          val (row, col) = solveNormalFormGame(
            MatrixGames.matchingPennies,
            5 * 1000, zeroSum = true)

          row.max should be lessThan 0.5 + epsilonProbability
          col.max should be lessThan 0.5 + epsilonProbability
        }

        "Coordination games" in {
          val beCoordinationSolution : Matcher[(Seq[Double], Seq[Double])] =
            new Matcher[(Seq[Double], Seq[Double])] {
              def apply[S <: (Seq[Double], Seq[Double])](t: Expectable[S]): MatchResult[S] = {
                val (row: Seq[Double], col: Seq[Double]) = t.value

                val isPureDiagonal =
                  if (row(0) < 0.3) {
                    row(0) < epsilonProbability && col(0) < epsilonProbability
                  } else if (row(1) < 0.3) {
                    row(1) < epsilonProbability && col(1) < epsilonProbability
                  } else ???

                result(isPureDiagonal, "is coordinated", "isn't coordinated", t)
              }
            }

          "Pure coordination" in {
            solveNormalFormGame(
              MatrixGames.pureCoordination,
              5
            ) should beCoordinationSolution
          }

          "Battle of the Sexes" in {
            solveNormalFormGame(
              MatrixGames.battleOfTheSexes,
              6
            ) should beCoordinationSolution
          }

          "Choosing sides" in {
            solveNormalFormGame(
              MatrixGames.choosingSides,
              10
            ) should beCoordinationSolution
          }

          "Stag hunt" in {
            solveNormalFormGame(
              MatrixGames.stagHunt,
              12
            ) should beCoordinationSolution
          }
        }
      }

      "Other matrix games" in {
        "Zero Sum" in {
          val (row, col) = solveNormalFormGame(
            MatrixGames.zeroSum,
            7 * 1000, zeroSum = true)

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
