package ao.learn.mst.gen5.solve

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.example.bandit.rps.RockPaperScissorsGame
import ao.learn.mst.gen5.example.bandit.rpsw.RockPaperScissorsWellGame
import ao.learn.mst.gen5.solve2.RegretSampler
import ao.learn.mst.gen5.cfr2.OutcomeRegretSampler

/**
 * 17/02/14 4:56 PM
 */
class RpsSolverSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  val epsilonProbability: Double =
    0.05


  //--------------------------------------------------------------------------------------------------------------------
  "Counterfactual Regret Minimization algorithm" should
  {
    def cfrAlgorithm[S, I, A](): RegretSampler[S, I, A] =
      new OutcomeRegretSampler[S, I, A]()


    "Solve single-info non-zero-sum rock-paper-scissors style games" in {
      def solveRpsGame[S, I, A](game: ExtensiveGame[S, I, A], actions: Int): Seq[Double] =
        SolverSpecUtils
          .solveWithSummary(game, cfrAlgorithm())
          .probabilities(0, actions)

      "Rock-paper-scissors" in {
        val optimalStrategy = solveRpsGame(
          RockPaperScissorsGame, 3)

        // (roughly) equal distribution
        optimalStrategy.min must be greaterThan(
          1.0/3 - epsilonProbability)
      }

      "Rock-paper-scissors-well" in {
        val optimalStrategy = solveRpsGame(
          RockPaperScissorsWellGame, 4)

        // rock is dominated
        optimalStrategy(0) must be lessThan epsilonProbability

        // mixing evenly between paper, scissors, and well is an equilibrium.
        optimalStrategy.max must be lessThan(
          1.0/3 + epsilonProbability)
      }
    }
  }
}
