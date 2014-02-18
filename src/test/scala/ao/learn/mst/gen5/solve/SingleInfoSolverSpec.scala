package ao.learn.mst.gen5.solve

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.cfr.{OutcomeSampling2CfrMinimizer, OutcomeSamplingCfrMinimizer, ChanceSampledCfrMinimizer}
import ao.learn.mst.gen5.example.bandit.deterministic.DeterministicBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.uniform.UniformBinaryBanditGame
import scala.util.Random
import ao.learn.mst.gen5.example.bandit.gaussian.GaussianBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.bernoulli.BernoulliBinaryBanditGame
import ao.learn.mst.gen5.ExtensiveGame


class SingleInfoSolverSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  val epsilonProbability:Double =
    0.01


  //--------------------------------------------------------------------------------------------------------------------
  "Counterfactual Regret Minimization algorithm" should
  {
    def cfrAlgorithm[S, I, A]() : ExtensiveSolver[S, I, A] =
//      new OutcomeSamplingCfrMinimizer[S, I, A]
      new OutcomeSampling2CfrMinimizer[S, I, A]


    "Solve singleton information-set games" in {
      def solveSingleInfoSetGame[S, I, A](game: ExtensiveGame[S, I, A], iterations: Int): Seq[Double] =
        SolverSpecUtils
          .solve(game, cfrAlgorithm(), iterations)
          .actionProbabilityMass(0)


      "Classical bandit setting" in {
        "Deterministic binary bandit" in {
          val optimalStrategy = solveSingleInfoSetGame(
            DeterministicBinaryBanditGame.plusMinusOne,
            1)

          optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
        }

        "Stochastic binary bandits" in {
          implicit val sourceOfRandomness = new Random

          "Uniform" in {
            val optimalStrategy = solveSingleInfoSetGame(
              UniformBinaryBanditGame.withAdvantageForTrue(0.05),
              5 * 1000)

            optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
          }

          "Bernoulli" in {
            val optimalStrategy = solveSingleInfoSetGame(
              BernoulliBinaryBanditGame.withAdvantageForTrue(0.05),
              7 * 1000)

            optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
          }

          "Gaussian" in {
            val optimalStrategy = solveSingleInfoSetGame(
              GaussianBinaryBanditGame.withAdvantageForTrue(0.05),
              20 * 1000)

            optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
          }
        }
      }
    }
  }
}
