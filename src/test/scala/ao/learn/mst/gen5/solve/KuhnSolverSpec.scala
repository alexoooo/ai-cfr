package ao.learn.mst.gen5.solve

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen5.example.ocp.KuhnGame
import ao.learn.mst.gen5.example.SimpleGameSolution
import ao.learn.mst.gen5.example.abstraction.AbstractionUtils
import ao.learn.mst.gen5.example.kuhn.card.KuhnCard
import ao.learn.mst.gen5.example.kuhn.card.KuhnCard.KuhnCard
import ao.learn.mst.gen5.example.kuhn.action._
import ao.learn.mst.gen5.cfr.{OutcomeSampling2CfrMinimizer, OutcomeSamplingCfrMinimizer}
import ao.learn.mst.gen5.example.kuhn.view.KuhnObservation
import ao.learn.mst.gen5.example.kuhn.action.KuhnActionSequence
import ao.learn.mst.gen5.example.kuhn.action.KuhnActionSequence.KuhnActionSequence
import ao.learn.mst.gen5.example.kuhn.state.KuhnState

/**
 * 17/02/14 3:06 PM
 */
class KuhnSolverSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  val epsilonProbability:Double =
    0.02

  "Counterfactual Regret Minimization algorithm with strategy averaging" should
  {
    def cfrAlgorithm[S, I, A]() : ExtensiveSolver[S, I, A] =
//      new OutcomeSamplingCfrMinimizer[S, I, A](true)
      new OutcomeSampling2CfrMinimizer[S, I, A](true)

    "Solve Kuhn Poker" in {
      val solution: SimpleGameSolution[Option[KuhnState], KuhnObservation, KuhnGenAction] =
        SimpleGameSolution.forGame(
          KuhnGame,
          cfrAlgorithm(),
          500 * 1000,
          display = false)

      val infoSets: Set[KuhnObservation] =
        AbstractionUtils.informationSets(KuhnGame)

      def kuhnStrategy(holeCard: KuhnCard, actionSequence: KuhnActionSequence, choice: KuhnDecision): Double = {
        solution.strategy.actionProbability(solution.abstraction, kuhnInfo(holeCard, actionSequence), choice)
      }

      def kuhnInfo(holeCard: KuhnCard, actionSequence: KuhnActionSequence): KuhnObservation =
        infoSets
          .find {
            case KuhnObservation(`holeCard`, `actionSequence`, _) => true
            case _ => false
          }.get

      "Avoiding dominated strategies" in {
        val firstPlayerFirstActionWithQueenPass =
          kuhnStrategy(KuhnCard.Queen, KuhnActionSequence.Empty, KuhnCheckFold)
        firstPlayerFirstActionWithQueenPass must be greaterThan(1.0 - epsilonProbability)

        val secondPlayerAfterPassWithQueenPass =
          kuhnStrategy(KuhnCard.Queen, KuhnActionSequence.Check, KuhnCheckFold)
        secondPlayerAfterPassWithQueenPass must be greaterThan(1.0 - epsilonProbability)

        val secondPlayerAfterPassWithKingBet =
          kuhnStrategy(KuhnCard.King, KuhnActionSequence.Check, KuhnCallRaise)
        secondPlayerAfterPassWithKingBet must be greaterThan(1.0 - epsilonProbability)

        val secondPlayerAfterBetWithKingBet =
          kuhnStrategy(KuhnCard.King, KuhnActionSequence.Raise, KuhnCallRaise)
        secondPlayerAfterBetWithKingBet must be greaterThan(1.0 - epsilonProbability)

        val firstPlayerCheckRaiseWithJackPass =
          kuhnStrategy(KuhnCard.Jack, KuhnActionSequence.CheckRaise, KuhnCheckFold)
        firstPlayerCheckRaiseWithJackPass must be greaterThan(1.0 - epsilonProbability)

        val secondPlayerAfterRaiseWithJackPass =
          kuhnStrategy(KuhnCard.Jack, KuhnActionSequence.Raise, KuhnCheckFold)
        secondPlayerAfterRaiseWithJackPass must be greaterThan(1.0 - epsilonProbability)
      }

      "With unique optimal strategy for second player" in {
        val secondPlayerAfterPassWithJackBet =
          kuhnStrategy(KuhnCard.Jack, KuhnActionSequence.Check, KuhnCallRaise)
        secondPlayerAfterPassWithJackBet must be greaterThan(1.0/3 - epsilonProbability)
        secondPlayerAfterPassWithJackBet must be lessThan(1.0/3 + epsilonProbability)

        val secondPlayerAfterBetWithQueenBet =
          kuhnStrategy(KuhnCard.Queen, KuhnActionSequence.Raise, KuhnCallRaise)
        secondPlayerAfterBetWithQueenBet must be greaterThan(1.0/3 - epsilonProbability)
        secondPlayerAfterBetWithQueenBet must be lessThan(1.0/3 + epsilonProbability)
      }

      "With one of the many optimal strategies for the first player" in {
        val betWithJack =
          kuhnStrategy(KuhnCard.Jack, KuhnActionSequence.Empty, KuhnCallRaise)

        val betWithQueenAfterPassBet =
          kuhnStrategy(KuhnCard.Queen, KuhnActionSequence.CheckRaise, KuhnCallRaise)

        val betWithKing =
          kuhnStrategy(KuhnCard.King, KuhnActionSequence.Empty, KuhnCallRaise)

        betWithJack must be lessThan(betWithKing / 3 + epsilonProbability)
        betWithJack must be greaterThan(betWithKing / 3 - epsilonProbability)

        betWithQueenAfterPassBet must be lessThan((1 + betWithKing) / 3 + epsilonProbability)
        betWithQueenAfterPassBet must be greaterThan((1 + betWithKing) / 3 - epsilonProbability)
      }
    }
  }
}