package ao.learn.mst.gen5

import ao.learn.mst.gen5.solve.ExtensiveSolver
import ao.learn.mst.gen5.cfr.ChanceSampledCfrMinimizer
import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.example.perfect.complete.PerfectCompleteGame
import ao.learn.mst.gen5.example.imperfect.ImperfectGame

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
      new ChanceSampledCfrMinimizer[S, I, A]

    "Solve basic small games" in {
      def solveGame[S, I, A](
        game       : ExtensiveGame[S, I, A],
        iterations : Int)
        : Seq[Seq[Double]] =
      {
        val solver : ExtensiveSolver[S, I, A] =
          cfrAlgorithm()

        val strategy : ExtensiveStrategyProfile =
          SolverSpecUtils.solve(game, solver, iterations)

        (0 until strategy.knownInformationSetCount)
          .map(strategy.actionProbabilityMass)
      }

      "Perfect and complete information" in {
        val solution = solveGame(
          PerfectCompleteGame,
          300)

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
    }
  }
}
