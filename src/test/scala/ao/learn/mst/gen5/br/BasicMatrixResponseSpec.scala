package ao.learn.mst.gen5.br

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.example.SimpleGameSolution
import ao.learn.mst.gen5.example.matrix._
import ao.learn.mst.gen5.example.matrix.MatrixGame
import ao.learn.mst.gen5.cfr.OutcomeSamplingCfrMinimizer

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
        iterations: Int
        ): (BestResponse[MatrixPlayer, MatrixAction], BestResponse[MatrixPlayer, MatrixAction]) =
    {
      val solution : SimpleGameSolution[MatrixState, MatrixPlayer, MatrixAction] =
        SimpleGameSolution.forGame(
          game,
          OutcomeSamplingCfrMinimizer(),
          iterations)

      val bestResponses : BestResponseProfile[MatrixPlayer, MatrixAction] =
        solution.response

      assert(bestResponses.bestResponses.length == 2)

      (bestResponses.bestResponses(0), bestResponses.bestResponses(1))
    }


    "Exist for deadlock game" in {
      val (rowResponse, columnResponse) =
        matrixGameResponses(MatrixGames.deadlock, 1)

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
