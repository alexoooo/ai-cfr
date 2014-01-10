package ao.learn.mst.gen5

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.example.matrix.MatrixGames
import ao.learn.mst.gen5.example.bandit.deterministic.DeterministicBinaryBanditGame
import ao.learn.mst.gen5.example.SimpleGameSolution
import ao.learn.mst.gen5.br.{BestResponse, BestResponseProfile}

/**
 * http://en.wikipedia.org/wiki/Best_response
 */
class BestResponseSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  "Best response strategy" should
  {
    "Apply to single single player deterministic games" in {

      val singlePlayerGame : ExtensiveGame[_, _, _] =
        DeterministicBinaryBanditGame.plusMinusOne

      val solution : SimpleGameSolution[_, _, _] =
        SimpleGameSolution.forGame(
          singlePlayerGame, 1)

      val bestResponses : BestResponseProfile[_, _] =
        solution.response

      "With a response for the player" in {
        bestResponses.bestResponses.size must be equalTo 1
      }

      val bestResponse : BestResponse[_, _] =
        bestResponses.bestResponses.head

      "With a game value of +1.0" in {
        bestResponse.value must be equalTo 1.0
      }
    }
  }
}
