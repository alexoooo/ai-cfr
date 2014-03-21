package ao.learn.mst.gen5.br

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.example.SimpleGameSolution
import ao.learn.mst.gen5.example.matrix._
import ao.learn.mst.gen5.example.matrix.MatrixGame
import ao.learn.mst.gen5.cfr.{ChanceSampledCfrMinimizer, OutcomeSamplingCfrMinimizer}
import ao.learn.mst.gen5.cfr2.OutcomeRegretSampler
import ao.learn.mst.gen5.solve2.RegretSampler

/**
 * 20/01/14 9:37 PM
 */
class BasicMatrixResponseSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  "Best response strategy for basic matrix games" should
  {
    def matrixGameResponses(
        game: MatrixGame,
        iterations: Int)
        : (BestResponse[MatrixPlayer, MatrixAction], BestResponse[MatrixPlayer, MatrixAction]) =
    {
      val solver: RegretSampler[MatrixState, MatrixPlayer, MatrixAction] =
        new OutcomeRegretSampler[MatrixState, MatrixPlayer, MatrixAction]()

      val solution: SimpleGameSolution[MatrixState, MatrixPlayer, MatrixAction] =
        SimpleGameSolution.forGame(
          game, solver, false, iterations)

      val bestResponses: BestResponseProfile[MatrixPlayer, MatrixAction] =
        BestResponseFinder.bestResponseProfile(
          game, solution.abstraction, solution.strategy)

      assert(bestResponses.bestResponses.length == 2)

      (bestResponses.bestResponses(0), bestResponses.bestResponses(1))
    }


    "Exist for deadlock game" in {
      val (rowResponse, columnResponse) =
        matrixGameResponses(MatrixGames.deadlock, 100)

      "With a game values of 2.0" in {
        rowResponse.value must be equalTo 2.0
        columnResponse.value must be equalTo 2.0
      }
    }

    "Exist for matching pennies game" in {
      val (rowResponse, columnResponse) =
        matrixGameResponses(MatrixGames.matchingPennies, 0)

      "With a game values of 0.0" in {
        rowResponse.value must be equalTo 0.0
        columnResponse.value must be equalTo 0.0
      }
    }
  }
}
