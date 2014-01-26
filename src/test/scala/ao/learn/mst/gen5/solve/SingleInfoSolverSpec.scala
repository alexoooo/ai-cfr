package ao.learn.mst.gen5.solve

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.learn.mst.gen5.cfr.{OtherSampledCfrMinimizer, ChanceSampledCfrMinimizer}
import ao.learn.mst.gen5.example.abstraction.{LosslessInfoLosslessDecisionAbstractionBuilder, OpaqueAbstractionBuilder}
import ao.learn.mst.gen5.node.Decision
import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.example.bandit.deterministic.DeterministicBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.uniform.UniformBinaryBanditGame
import scala.util.Random
import ao.learn.mst.gen5.example.bandit.gaussian.GaussianBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.bernoulli.BernoulliBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.rps.RockPaperScissorsGame
import ao.learn.mst.gen5.example.bandit.rpsw.RockPaperScissorsWellGame
import ao.learn.mst.gen5.ExtensiveGame


class SingleInfoSolverSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  val epsilonProbability:Double =
//    0.01
    0.02


  //--------------------------------------------------------------------------------------------------------------------
  "Counterfactual Regret Minimization algorithm" should
  {
    def cfrAlgorithm[S, I, A]() : ExtensiveSolver[S, I, A] =
//      new ChanceSampledCfrMinimizer[S, I, A]
      new OtherSampledCfrMinimizer[S, I, A]


    "Solve singleton information-set games:" in {
      def solveSingletonInformationSetGame[S, I, A](
          game            : ExtensiveGame[S, I, A],
          iterations      : Int,
          expectedActions : Option[Int] = None): Seq[Double] =
      {
        val solver : ExtensiveSolver[S, I, A] =
          cfrAlgorithm()

        val strategy : ExtensiveStrategyProfile =
          SolverSpecUtils.solve(game, solver, iterations)

        val actionProbabilities : Seq[Double] =
          expectedActions match {
            case None    => strategy.actionProbabilityMass(0)
            case Some(c) => strategy.actionProbabilityMass(0, c)
          }

        actionProbabilities
      }

      "Classical bandit setting" in {
        "Deterministic binary bandit" in {
          val optimalStrategy = solveSingletonInformationSetGame(
            DeterministicBinaryBanditGame.plusMinusOne,
            64)

          optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
        }

        "Stochastic binary bandits" in {
          implicit val sourceOfRandomness = new Random

          "Uniform" in {
            val optimalStrategy = solveSingletonInformationSetGame(
              UniformBinaryBanditGame.withAdvantageForTrue(0.05),
              15 * 1000)

            optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
          }

          "Bernoulli" in {
            val optimalStrategy = solveSingletonInformationSetGame(
              BernoulliBinaryBanditGame.withAdvantageForTrue(0.05),
              43 * 1000)

            optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
          }

          "Gaussian" in {
            val optimalStrategy = solveSingletonInformationSetGame(
                GaussianBinaryBanditGame.withAdvantageForTrue(0.05),
                250 * 1000)

            optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
          }
        }
      }

      "Rock-paper-scissors" in {
        val optimalStrategy = solveSingletonInformationSetGame(
          RockPaperScissorsGame,
          150 * 1000)

        // (roughly) equal distribution
        optimalStrategy.min must be greaterThan(
          1.0/3 - epsilonProbability)
      }

      "Rock-paper-scissors-well" in {
        val optimalStrategy = solveSingletonInformationSetGame(
          RockPaperScissorsWellGame,
          250 * 1000)

        // rock is dominated
        optimalStrategy(0) must be lessThan epsilonProbability

        // mixing evenly between paper, scissors, and well is an equilibrium.
        optimalStrategy.max must be lessThan(
          1.0/3 + epsilonProbability)
      }
    }
  }
}
