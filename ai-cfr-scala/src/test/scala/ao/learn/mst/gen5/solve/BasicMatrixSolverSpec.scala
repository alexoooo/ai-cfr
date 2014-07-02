package ao.learn.mst.gen5.solve

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.example.matrix._
import scala._
import org.specs2.matcher.{Expectable, Matcher, MatchResult}
import ao.learn.mst.gen5.example.matrix.MatrixGame
import ao.learn.mst.gen5.solve2.{RegretSampler, RegretMinimizer}
import ao.learn.mst.gen5.abstraction.LosslessInfoLosslessDecisionAbstractionBuilder
import ao.learn.mst.gen5.cfr2.OutcomeRegretSampler
import scala.util.Random

/**
 * http://en.wikipedia.org/wiki/Normal_form_game
 */
class BasicMatrixSolverSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  val epsilonProbability:Double =
    0.01

  def randomness: Random = {
    val seed: Long =
      (Long.MaxValue * math.random).toLong
    println(s">> Seed: $seed")
    new Random(seed)
//    new Random(7902048605862579200L)
  }


  //--------------------------------------------------------------------------------------------------------------------
  "Counterfactual Regret Minimization algorithm" should
  {
    "Solve normal-form matrix games" in {
      def solveNormalFormGame(
          name       : String,
          game       : MatrixGame,
          zeroSum    : Boolean = false): (Seq[Double], Seq[Double]) =
      {
        val solver: RegretSampler[MatrixState, MatrixPlayer, MatrixAction] =
          new OutcomeRegretSampler[MatrixState, MatrixPlayer, MatrixAction](randomness = randomness)

        val minimizer = new RegretMinimizer(game,
          LosslessInfoLosslessDecisionAbstractionBuilder.generate(game),
          averageStrategy = zeroSum)

        val iterations = minimizer.iterateToConvergence(solver)
        print(s"> $name converged in $iterations iterations\n\n")

        val strategy = minimizer.strategyView

        val rowActionProbabilities : Seq[Double] =
          strategy.probabilities(0, game.rowPlayerPayoffs.rows())

        val columnActionProbabilities : Seq[Double] =
          strategy.probabilities(1, game.columnPlayerPayoffs.columns())

        (rowActionProbabilities, columnActionProbabilities)
      }

      "Classic 2x2 games" in {
        "Deadlock" in {
          val (row, col) = solveNormalFormGame(
            "Deadlock", MatrixGames.deadlock)

          row(0) should be lessThan epsilonProbability
          col(0) should be lessThan epsilonProbability
        }

        "Prisoner's Dilemma" in {
          val (row, col) = solveNormalFormGame(
            "Prisoner's Dilemma", MatrixGames.prisonersDilemma)

          row(0) should be lessThan epsilonProbability
          col(0) should be lessThan epsilonProbability
        }

        "Matching Pennies" in {
          val (row, col) = solveNormalFormGame(
            "Matching Pennies", MatrixGames.matchingPennies,
            zeroSum = true)

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
              "Pure coordination", MatrixGames.pureCoordination
            ) should beCoordinationSolution
          }

          "Battle of the Sexes" in {
            solveNormalFormGame(
              "Battle of the Sexes", MatrixGames.battleOfTheSexes
            ) should beCoordinationSolution
          }

          "Choosing Sides" in {
            solveNormalFormGame(
              "Choosing sides", MatrixGames.choosingSides
            ) should beCoordinationSolution
          }

          "Stag Hunt" in {
            solveNormalFormGame(
              "Stag Hunt", MatrixGames.stagHunt
            ) should beCoordinationSolution
          }
        }

        "Anti-coordination games" in {
          "Chicken (aka. Hawk-dove)" in {
            val (row, col) = solveNormalFormGame(
              "Chicken", MatrixGames.chicken)

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
            "Zero Sum", MatrixGames.zeroSum,
            zeroSum = true)

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
