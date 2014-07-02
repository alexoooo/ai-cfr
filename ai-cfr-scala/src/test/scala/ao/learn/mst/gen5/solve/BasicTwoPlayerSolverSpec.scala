package ao.learn.mst.gen5.solve

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.example.perfect.complete.PerfectCompleteGame
import ao.learn.mst.gen5.example.imperfect.ImperfectGame
import ao.learn.mst.gen5.example.sig.SignalingGame
import ao.learn.mst.gen5.example.burning.BurningGame
import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.state.MixedStrategy
import ao.learn.mst.gen5.solve2.RegretSampler
import ao.learn.mst.gen5.cfr2.OutcomeRegretSampler
import scala.util.Random

/**
 *
 */
class BasicTwoPlayerSolverSpec
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
//    new Random(6476021004647212032L)
  }


  //--------------------------------------------------------------------------------------------------------------------
  "Counterfactual Regret Minimization algorithm" should
  {
    def cfrAlgorithm[S, I, A](): RegretSampler[S, I, A] =
//      new ChanceSampledCfrMinimizer[S, I, A]
//      new OutcomeSamplingCfrMinimizer[S, I, A]
//      new OutcomeSampling2CfrMinimizer[S, I, A]
//      new ProbingCfrMinimizer[S, I, A]
      new OutcomeRegretSampler[S, I, A](randomness = randomness)

    "Solve basic small games" in {
      def solveGame[S, I, A](game: ExtensiveGame[S, I, A], iterations: Int): MixedStrategy =
        SolverSpecUtils.solveWithSummary(game, cfrAlgorithm())

      "Perfect and complete information" in {
        val solution = solveGame(
          PerfectCompleteGame,
          2 * 1000)

        val playerOne     = solution.probabilities(0, 2)
        val playerTwoDown = solution.probabilities(1, 2)
        val playerTwoUp   = solution.probabilities(2, 2)

        playerOne(0) must be lessThan epsilonProbability
        playerTwoUp(1) must be lessThan epsilonProbability

        // there is no guarantee for actions that opponent will not allow to reach
        //playerTwoDown(0) must be lessThan 0.5
        //playerTwoDown(0) must be lessThan epsilonProbability
      }

      "Imperfect information" in {
        val solution = solveGame(
          ImperfectGame,
          1000)

        val playerOne = solution.probabilities(0, 2)
        val playerTwo = solution.probabilities(1, 2)

        playerOne(1) must be lessThan epsilonProbability
        playerTwo(0) must be lessThan epsilonProbability
      }

      "Signaling" in {
        val solution = solveGame(
          SignalingGame,
          1000)

        val senderFalse   = solution.probabilities(0, 2)
        val senderTrue    = solution.probabilities(1, 2)
        val receiverFalse = solution.probabilities(2, 2)
        val receiverTrue  = solution.probabilities(3, 2)

        if (senderFalse(0) > senderFalse(1)) {
          senderFalse  (1) must be lessThan epsilonProbability
          senderTrue   (0) must be lessThan epsilonProbability
          receiverFalse(1) must be lessThan epsilonProbability
          receiverTrue (0) must be lessThan epsilonProbability
        } else {
          senderFalse  (0) must be lessThan epsilonProbability
          senderTrue   (1) must be lessThan epsilonProbability
          receiverFalse(0) must be lessThan epsilonProbability
          receiverTrue (1) must be lessThan epsilonProbability
        }
      }

      "Money-burning Battle of the Sexes" in {
        val solution = solveGame(
          BurningGame,
          10 * 1000)

        val burningDecision = solution.probabilities(0, 2)
        val noBurnRowPlay   = solution.probabilities(1, 2)
        val noBurnColPlay   = solution.probabilities(3, 2)

        burningDecision(0) must be greaterThan 1.0 - epsilonProbability
        noBurnRowPlay(0) must be greaterThan 1.0 - epsilonProbability
        noBurnColPlay(0) must be greaterThan 1.0 - epsilonProbability
      }
    }
  }
}
