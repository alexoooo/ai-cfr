package ao.learn.mst.gen5.solve

import ao.learn.mst.gen5.cfr.{OutcomeSampling2CfrMinimizer, ProbingCfrMinimizer, OutcomeSamplingCfrMinimizer, ChanceSampledCfrMinimizer}
import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.example.monty.{BasicMontyHallGame, MontyHallGame}
import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.state.MixedStrategy
import ao.learn.mst.gen5.solve2.{RegretMinimizer, RegretSampler}
import ao.learn.mst.gen5.cfr2.OutcomeRegretSampler
import ao.learn.mst.gen5.abstraction.LosslessInfoLosslessDecisionAbstractionBuilder
import scala.util.Random

/**
 *
 */
class BasicSinglePlayerSolverSpec
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
//    new Random(2471739060473428992L)
  }



  //--------------------------------------------------------------------------------------------------------------------
  "Counterfactual Regret Minimization algorithm" should
  {
    def cfrAlgorithm[S, I, A](): RegretSampler[S, I, A] =
      new OutcomeRegretSampler[S, I, A](randomness = randomness)


    "Solve basic small games" in {
      def solveGame[S, I, A](game: ExtensiveGame[S, I, A]): MixedStrategy =
        SolverSpecUtils.solveWithSummary(game, cfrAlgorithm())

      "Basic Monty Hall problem" in {
        val solution = solveGame(
          BasicMontyHallGame)

        val switchDecision =
          solution.probabilities(1, 2)

        switchDecision(0) must be lessThan epsilonProbability
      }

      "Monty Hall problem" in {
        val solution: MixedStrategy = solveGame(
          MontyHallGame)

        // Info set sequence?
        //  0 MontyPickDoorInfo
        //  1 MontySwitchDoorInfo(0,1)
        //  2 MontySwitchDoorInfo(0,2)
        //  3 MontySwitchDoorInfo(1,2)
        //  4 MontySwitchDoorInfo(2,1)
        //  5 MontySwitchDoorInfo(1,0)
        //  6 MontySwitchDoorInfo(2,0)

        val initialDoorChoice: Seq[Double] =
          solution.probabilities(0, 3)

        val doorChoiceInfos: Seq[Seq[Int]] =
          Seq(Seq(1, 2), Seq(3, 5), Seq(4, 6))

        val switchChoices: Seq[Seq[Double]] =
          doorChoiceInfos.map(infos => infos.map(solution.probabilities(_, 2)(1)))

        foreach (0 until initialDoorChoice.length) {c =>
          if (initialDoorChoice(c) > epsilonProbability) {
            switchChoices(c)(0) must be greaterThan 1.0 - epsilonProbability
            switchChoices(c)(1) must be greaterThan 1.0 - epsilonProbability
          } else ok
        }
      }
    }
  }
}
