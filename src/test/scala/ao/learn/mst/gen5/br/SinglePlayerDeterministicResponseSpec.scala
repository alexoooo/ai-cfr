package ao.learn.mst.gen5.br

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.example.bandit.deterministic._
import ao.learn.mst.gen5.example.SimpleGameSolution
import ao.learn.mst.gen5.example.bandit.BinaryBanditState
import ao.learn.mst.gen5.example.bandit.deterministic.DeterministicBinaryBranchState
import scala.Some
import ao.learn.mst.gen5.ExtensiveGame

/**
 * http://en.wikipedia.org/wiki/Best_response
 */
class SinglePlayerDeterministicResponseSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  "Best response strategy in simple single-player deterministic game" should
  {
    "Exist for deterministic binary bandit game" in {
      val game : ExtensiveGame[BinaryBanditState, Unit, Boolean] =
        DeterministicBinaryBanditGame.plusMinusOne

      val solution : SimpleGameSolution[BinaryBanditState, Unit, Boolean] =
        SimpleGameSolution.forGame(
          game, 0)

      val bestResponses : BestResponseProfile[Unit, Boolean] =
        solution.response

      "With one response" in {
        bestResponses.bestResponses.size must be equalTo 1
      }

      val bestResponse : BestResponse[Unit, Boolean] =
        bestResponses.bestResponses.head

      "With a game value of +1.0" in {
        bestResponse.value must be equalTo 1.0
      }

      "Playing 'true" in {
        bestResponse.strategy(game.initialState) must be equalTo true
      }
    }

    "Exist for deterministic binary branch game" in {
      val game : ExtensiveGame[DeterministicBinaryBranchState, DeterministicBinaryBranchState, Boolean] =
        DeterministicBinaryBranchGame

      val solution : SimpleGameSolution[DeterministicBinaryBranchState, DeterministicBinaryBranchState, Boolean] =
        SimpleGameSolution.forGame(
          game, 0)

      val bestResponses : BestResponseProfile[DeterministicBinaryBranchState, Boolean] =
        solution.response

      "With one response" in {
        bestResponses.bestResponses.size must be equalTo 1
      }

      val bestResponse : BestResponse[DeterministicBinaryBranchState, Boolean] =
        bestResponses.bestResponses.head

      "With a game value of +3.0" in {
        bestResponse.value must be equalTo 3.0
      }

      "Playing 'true'" in {
        bestResponse.strategy(game.initialState) must be equalTo true
      }

      "Knowing to play 'true' after sub-optimal 'false" in {
        bestResponse.strategy(
          DeterministicBinaryBranchState(Some(false), None)
        ) must be equalTo true
      }
    }

    "Exist for deterministic binary fork game" in {
      val game : ExtensiveGame[DeterministicBinaryForkState, DeterministicBinaryForkState, Boolean] =
        DeterministicBinaryForkGame

      val solution : SimpleGameSolution[DeterministicBinaryForkState, DeterministicBinaryForkState, Boolean] =
        SimpleGameSolution.forGame(
          game, 0)

      val bestResponses : BestResponseProfile[DeterministicBinaryForkState, Boolean] =
        solution.response

      "With one response" in {
        bestResponses.bestResponses.size must be equalTo 1
      }

      val bestResponse : BestResponse[DeterministicBinaryForkState, Boolean] =
        bestResponses.bestResponses.head

      "With a game value of +3.0" in {
        bestResponse.value must be equalTo 4.0
      }

      "Playing 'true' -> 'true'" in {
        bestResponse.strategy(game.initialState) must be equalTo true

        bestResponse.strategy(
          DeterministicBinaryForkState(Some(true), None)
        ) must be equalTo true
      }

      "Knowing to play 'true' after sub-optimal 'false'" in {
        bestResponse.strategy(
          DeterministicBinaryForkState(Some(false), None)
        ) must be equalTo true
      }
    }
  }
}
