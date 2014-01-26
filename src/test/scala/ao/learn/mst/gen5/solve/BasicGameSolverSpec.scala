package ao.learn.mst.gen5.solve

import ao.learn.mst.gen5.solve.ExtensiveSolver
import ao.learn.mst.gen5.cfr.{OtherSampledCfrMinimizer, ChanceSampledCfrMinimizer}
import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.example.perfect.complete.PerfectCompleteGame
import ao.learn.mst.gen5.example.imperfect.ImperfectGame
import ao.learn.mst.gen5.example.sig.SignalingGame
import ao.learn.mst.gen5.example.monty.{BasicMontyHallGame, MontyHallGame}
import ao.learn.mst.gen5.example.burning.BurningGame
import ao.learn.mst.gen5.ExtensiveGame

/**
 *
 */
class BasicGameSolverSpec
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
      new OtherSampledCfrMinimizer[S, I, A]

    "Solve basic small games" in {
      def solveGame[S, I, A](game : ExtensiveGame[S, I, A], iterations : Int) : Seq[Seq[Double]] =
        SolverSpecUtils.flatSolve(game, cfrAlgorithm(), iterations)

      "Perfect and complete information" in {
        val solution = solveGame(
          PerfectCompleteGame,
          400)

        val playerOne     = solution(0)
        val playerTwoDown = solution(1)
        val playerTwoUp   = solution(2)

        playerOne(0) must be lessThan epsilonProbability
        playerTwoUp(1) must be lessThan epsilonProbability

        //playerTwoDown(0) must be lessThan playerTwoDown(1)/2
        playerTwoDown(0) must be lessThan epsilonProbability
      }

      "Imperfect information" in {
        val solution = solveGame(
          ImperfectGame,
          110)

        val playerOne = solution(0)
        val playerTwo = solution(1)

        playerOne(1) must be lessThan epsilonProbability
        playerTwo(0) must be lessThan epsilonProbability
      }

      "Signaling" in {
        val solution = solveGame(
          SignalingGame,
          30)

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

      "Basic Monty Hall problem" in {
        val solution = solveGame(
          BasicMontyHallGame,
          4)

        val switchDecision =
          solution(1)

        switchDecision(0) must be lessThan epsilonProbability
      }

      "Monty Hall problem" in {
        val solution : Seq[Seq[Double]] = solveGame(
          MontyHallGame,
          200)

        val initialDoorChoice : Seq[Double] =
          solution(0)

        val switchChoices : Seq[Seq[Double]] =
          solution.drop(1)

        foreach (0 until initialDoorChoice.length) {c =>
          if (initialDoorChoice(c) > epsilonProbability) {
            switchChoices(c)(0) must be lessThan epsilonProbability
          } else ok
        }
      }

      "Money-burning Battle of the Sexes" in {
        val solution : Seq[Seq[Double]] = solveGame(
          BurningGame,
          5)

        val burningDecision = solution(0)
        val noBurnRowPlay   = solution(1)
        val noBurnColPlay   = solution(3)

        burningDecision(1) must be lessThan epsilonProbability
        noBurnRowPlay  (1) must be lessThan epsilonProbability
        noBurnColPlay  (1) must be lessThan epsilonProbability
      }
    }
  }
}
