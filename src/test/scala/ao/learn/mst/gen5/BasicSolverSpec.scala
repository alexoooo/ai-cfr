package ao.learn.mst.gen5

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.learn.mst.gen5.cfr.ChanceSampledCfrMinimizer
import ao.learn.mst.gen5.example.abstraction.{SingleStateLosslessDecisionAbstractionBuilder, OpaqueAbstractionBuilder}
import ao.learn.mst.gen5.node.Decision
import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.example.simple.deterministic.DeterministicBinaryBanditGame
import ao.learn.mst.gen5.example.simple.uniform.UniformBinaryBanditGame
import scala.util.Random
import ao.learn.mst.gen5.example.simple.gaussian.GaussianBinaryBanditGame
import ao.learn.mst.gen5.example.simple.bernoulli.BernoulliBinaryBanditGame
import ao.learn.mst.gen5.example.simple.rps.RockPaperScissorsGame
import ao.learn.mst.gen5.example.simple.rps.RockPaperScissorsGame
import ao.learn.mst.gen5.example.simple.rpsw.RockPaperScissorsWellGame


class BasicSolverSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  val epsilonProbability:Double =
      0.01
//    0.05


  //--------------------------------------------------------------------------------------------------------------------
  "Counterfactual Regret Minimization algorithm" should
  {
    def cfrAlgorithm[S, I, A]() : ExtensiveSolver[S, I, A] =
      new ChanceSampledCfrMinimizer[S, I, A]


    "Solve singleton information set games" in {
      def solveSingletonInformationSetGame[S, I, A](
          game       : ExtensiveGame[S, I, A],
          iterations : Int): Seq[Double] =
      {
        val solver : ExtensiveSolver[S, I, A] =
          cfrAlgorithm()

        val solution : SolutionApproximation[I, A] =
          solver.initialSolution(game)

        val abstractionBuilder : OpaqueAbstractionBuilder =
          new SingleStateLosslessDecisionAbstractionBuilder

        val abstraction : ExtensiveAbstraction[I, A] =
          abstractionBuilder.generate(game)

        for (i <- 1 to iterations) {
          solution.optimize(abstraction)
        }

        val strategy : ExtensiveStrategyProfile =
          solution.strategy

        val informationSet : I =
          game.node(game.initialState) match {
            case Decision(_, infoSet, _) => infoSet
            case n => throw new Error(s"Unexpected node: $n")
          }

        val actionCount : Int =
          abstraction.actionCount(informationSet)

        val actionProbabilities : Seq[Double] =
          strategy.actionProbabilityMass(0, actionCount)

        actionProbabilities
      }

      "Classical bandit setting" in {
        "Deterministic binary bandit" in {
          val optimalStrategy = solveSingletonInformationSetGame(
            DeterministicBinaryBanditGame.plusMinusOne,
            64)

          optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
        }

        "Stochastic bandits" in {
          implicit val sourceOfRandomness = new Random

          "Uniform binary" in {
            val optimalStrategy = solveSingletonInformationSetGame(
              UniformBinaryBanditGame.withAdvantageForTrue(0.05),
              14 * 1000)

            optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
          }

          "Bernoulli binary" in {
            val optimalStrategy = solveSingletonInformationSetGame(
              BernoulliBinaryBanditGame.withAdvantageForTrue(0.05),
              43 * 1000)

            optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
          }

          "Gaussian binary" in {
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
          0)

        // (roughly) equal distribution
        optimalStrategy.min must be greaterThan(
          1.0/3 - epsilonProbability)
      }

      "Rock-paper-scissors-well" in {
        val optimalStrategy = solveSingletonInformationSetGame(
          RockPaperScissorsWellGame,
          100)

        // (roughly) equal distribution
        optimalStrategy(0) must be lessThan epsilonProbability

        optimalStrategy.max must be lessThan(
          1.0/3 + epsilonProbability)
      }
    }
  }
}
