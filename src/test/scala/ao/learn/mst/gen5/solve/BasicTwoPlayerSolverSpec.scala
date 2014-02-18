package ao.learn.mst.gen5.solve

import ao.learn.mst.gen5.cfr.{OutcomeSampling2CfrMinimizer, ProbingCfrMinimizer, OutcomeSamplingCfrMinimizer}
import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.example.perfect.complete.PerfectCompleteGame
import ao.learn.mst.gen5.example.imperfect.ImperfectGame
import ao.learn.mst.gen5.example.sig.SignalingGame
import ao.learn.mst.gen5.example.burning.BurningGame
import ao.learn.mst.gen5.ExtensiveGame

/**
 *
 */
class BasicTwoPlayerSolverSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  val epsilonProbability:Double =
    0.01


  //--------------------------------------------------------------------------------------------------------------------
  "Counterfactual Regret Minimization algorithm" should
  {
    def cfrAlgorithm[S, I, A]() : ExtensiveSolver[S, I, A] =
//      new ChanceSampledCfrMinimizer[S, I, A]
//      new OutcomeSamplingCfrMinimizer[S, I, A]
      new OutcomeSampling2CfrMinimizer[S, I, A]
//      new ProbingCfrMinimizer[S, I, A]

    "Solve basic small games" in {
      def solveGame[S, I, A](game : ExtensiveGame[S, I, A], iterations : Int) : Seq[Seq[Double]] =
        SolverSpecUtils.flatSolve(game, cfrAlgorithm(), iterations)

      "Perfect and complete information" in {
        val solution = solveGame(
          PerfectCompleteGame,
          1000)

        val playerOne     = solution(0)
        val playerTwoDown = solution(1)
        val playerTwoUp   = solution(2)

        playerOne(0) must be lessThan epsilonProbability
        playerTwoUp(1) must be lessThan epsilonProbability

        // there is no guarantee for actions that opponent will not allow to reach
        playerTwoDown(0) must be lessThan 0.5
        //playerTwoDown(0) must be lessThan epsilonProbability
      }

      "Imperfect information" in {
        val solution = solveGame(
          ImperfectGame,
          1000)

        val playerOne = solution(0)
        val playerTwo = solution(1)

        playerOne(1) must be lessThan epsilonProbability
        playerTwo(0) must be lessThan epsilonProbability
      }

      "Signaling" in {
        val solution = solveGame(
          SignalingGame,
          1000)

        val senderFalse   = solution(0)
        val senderTrue    = solution(1)
        val receiverFalse = solution(2)
        val receiverTrue  = solution(3)

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
        val solution : Seq[Seq[Double]] = solveGame(
          BurningGame,
          10 * 1000)

        val burningDecision = solution(0)
        val noBurnRowPlay   = solution(1)
        val noBurnColPlay   = solution(3)

        burningDecision.lift(1).getOrElse(0.0) must be lessThan epsilonProbability
        noBurnRowPlay  .lift(1).getOrElse(0.0) must be lessThan epsilonProbability
        noBurnColPlay  .lift(1).getOrElse(0.0) must be lessThan epsilonProbability
      }
    }
  }
}
