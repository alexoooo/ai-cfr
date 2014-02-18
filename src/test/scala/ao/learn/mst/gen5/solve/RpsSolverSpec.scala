package ao.learn.mst.gen5.solve

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.cfr.{OutcomeSampling2CfrMinimizer, OutcomeSamplingCfrMinimizer}
import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.example.bandit.rps.RockPaperScissorsGame
import ao.learn.mst.gen5.example.bandit.rpsw.RockPaperScissorsWellGame

/**
 * 17/02/14 4:56 PM
 */
class RpsSolverSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  val epsilonProbability: Double =
    0.03


  //--------------------------------------------------------------------------------------------------------------------
  "Counterfactual Regret Minimization algorithm" should
  {
    def cfrAlgorithm[S, I, A]() : ExtensiveSolver[S, I, A] =
//      new OutcomeSamplingCfrMinimizer[S, I, A]
      new OutcomeSampling2CfrMinimizer[S, I, A]


    "Solve single-info non-zero-sum rock-paper-scissors style games" in {
      def solveRpsGame[S, I, A](game: ExtensiveGame[S, I, A], iterations: Int): Seq[Double] =
        SolverSpecUtils
          .solve(game, cfrAlgorithm(), iterations)
          .actionProbabilityMass(0)

      "Rock-paper-scissors" in {
        val optimalStrategy = solveRpsGame(
          RockPaperScissorsGame,
          400 * 1000)

        // (roughly) equal distribution
        optimalStrategy.min must be greaterThan(
          1.0/3 - epsilonProbability)
      }

      "Rock-paper-scissors-well" in {
        val optimalStrategy = solveRpsGame(
          RockPaperScissorsWellGame,
          500 * 1000)

        // rock is dominated
        optimalStrategy(0) must be lessThan epsilonProbability

        // mixing evenly between paper, scissors, and well is an equilibrium.
        optimalStrategy.max must be lessThan(
          1.0/3 + epsilonProbability)
      }
    }
  }
}
