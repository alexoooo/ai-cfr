package ao.learn.mst.gen5.solve

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.example.bandit.deterministic.DeterministicBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.uniform.UniformBinaryBanditGame
import scala.util.Random
import ao.learn.mst.gen5.example.bandit.gaussian.GaussianBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.bernoulli.BernoulliBinaryBanditGame
import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.solve2.RegretSampler
import ao.learn.mst.gen5.cfr2.OutcomeRegretSampler


class SingleInfoSolverSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  val epsilonProbability:Double =
    0.01


  //--------------------------------------------------------------------------------------------------------------------
  "Counterfactual Regret Minimization algorithm" should
  {
    def cfrAlgorithm[S, I, A](): RegretSampler[S, I, A] =
      new OutcomeRegretSampler[S, I, A]()


    "Solve singleton information-set games" in {
      def solveSingleInfoBinaryGame[S, I, A](game: ExtensiveGame[S, I, A]): Seq[Double] =
        SolverSpecUtils
          .solveWithSummary(game, cfrAlgorithm())
          .probabilities(0, 2)


      "Classical bandit setting" in {
        "Deterministic binary bandit" in {
          val optimalStrategy = solveSingleInfoBinaryGame(
            DeterministicBinaryBanditGame.plusMinusOne)

          optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
        }

        "Stochastic binary bandits" in {
          implicit val sourceOfRandomness = new Random

          "Uniform" in {
            val optimalStrategy = solveSingleInfoBinaryGame(
              UniformBinaryBanditGame.withAdvantageForTrue(0.05))

            optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
          }

          "Bernoulli" in {
            val optimalStrategy = solveSingleInfoBinaryGame(
              BernoulliBinaryBanditGame.withAdvantageForTrue(0.05))

            optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
          }

          "Gaussian" in {
            val optimalStrategy = solveSingleInfoBinaryGame(
              GaussianBinaryBanditGame.withAdvantageForTrue(0.05))

            optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
          }
        }
      }
    }
  }
}
