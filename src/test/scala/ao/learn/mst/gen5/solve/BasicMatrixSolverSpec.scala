package ao.learn.mst.gen5.solve

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.example.matrix._
import scala._
import org.specs2.matcher.{Expectable, Matcher, MatchResult}
import ao.learn.mst.gen5.example.matrix.MatrixGame
import ao.learn.mst.gen5.cfr.OutcomeSampling2CfrMinimizer

/**
 * http://en.wikipedia.org/wiki/Normal_form_game
 */
class BasicMatrixSolverSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  val epsilonProbability:Double =
//    0.01
    0.03


  //--------------------------------------------------------------------------------------------------------------------
  "Counterfactual Regret Minimization algorithm" should
  {
    "Solve normal-form matrix games" in {
      def solveNormalFormGame(
          game       : MatrixGame,
          iterations : Int,
          zeroSum    : Boolean = false): (Seq[Double], Seq[Double]) =
      {
        val solver: ExtensiveSolver[MatrixState, MatrixPlayer, MatrixAction] =
//          new ChanceSampledCfrMinimizer[S, I, A](zeroSum)
//          new OutcomeSamplingCfrMinimizer[S, I, A](zeroSum)
          new OutcomeSampling2CfrMinimizer[MatrixState, MatrixPlayer, MatrixAction](zeroSum)

        val (strategy, abstraction) =
          SolverSpecUtils.solveWithAbstraction(game, solver, iterations)

        val rowActionProbabilities : Seq[Double] =
          strategy.probabilities(0, abstraction.actionCount(RowPlayer))

        val columnActionProbabilities : Seq[Double] =
          strategy.probabilities(1, abstraction.actionCount(ColumnPlayer))

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
            1000)

          row(0) should be lessThan epsilonProbability
          col(0) should be lessThan epsilonProbability
        }

        "Matching Pennies" in {
          val (row, col) = solveNormalFormGame(
            MatrixGames.matchingPennies,
            30 * 1000, zeroSum = true)

          row.max should be lessThan 0.5 + epsilonProbability
          col.max should be lessThan 0.5 + epsilonProbability
        }

        "Coordination games" in {
          val beCoordinationSolution : Matcher[(Seq[Double], Seq[Double])] =
            new Matcher[(Seq[Double], Seq[Double])] {
              def apply[S <: (Seq[Double], Seq[Double])](t: Expectable[S]): MatchResult[S] = {
                val (row: Seq[Double], col: Seq[Double]) = t.value

                val isPureDiagonal =
                  if (row(0) < 0.2) {
                    row(0) < epsilonProbability && col(0) < epsilonProbability
                  } else if (row(1) < 0.2) {
                    row(1) < epsilonProbability && col(1) < epsilonProbability
                  } else {
                    throw new UnsupportedOperationException(s"$row | $col")
                  }

                result(isPureDiagonal, "is coordinated", "isn't coordinated", t)
              }
            }

          "Pure coordination" in {
            solveNormalFormGame(
              MatrixGames.pureCoordination,
              20
            ) should beCoordinationSolution
          }

          "Battle of the Sexes" in {
            solveNormalFormGame(
              MatrixGames.battleOfTheSexes,
              20
            ) should beCoordinationSolution
          }

          "Choosing sides" in {
            solveNormalFormGame(
              MatrixGames.choosingSides,
              15
            ) should beCoordinationSolution
          }

          "Stag hunt" in {
            solveNormalFormGame(
              MatrixGames.stagHunt,
              100
            ) should beCoordinationSolution
          }
        }

        "Anti-coordination games" in {
          "Chicken (aka. Hawk-dove)" in {
            val (row, col) = solveNormalFormGame(
              MatrixGames.chicken,
              2 * 1000)

            if (row(0) < 0.2) {
              row(0) must be lessThan epsilonProbability
              col(1) must be lessThan epsilonProbability
            } else if (row(1) < 0.2) {
              row(1) must be lessThan epsilonProbability
              col(0) must be lessThan epsilonProbability
            } else {
              throw new UnsupportedOperationException(s"!!: $row | $col")
            }
          }
        }
      }

      "Other matrix games" in {
        "Zero Sum" in {
          val (row, col) = solveNormalFormGame(
            MatrixGames.zeroSum,
            300 * 1000, zeroSum = true)

          row(0) must be greaterThan(4.0/7 - epsilonProbability)
          row(1) must be greaterThan(3.0/7 - epsilonProbability)

          col(0) must be lessThan epsilonProbability
          col(1) must be greaterThan(4.0/7 - epsilonProbability)
          col(2) must be greaterThan(3.0/7 - epsilonProbability)
        }
      }
    }
  }
}
