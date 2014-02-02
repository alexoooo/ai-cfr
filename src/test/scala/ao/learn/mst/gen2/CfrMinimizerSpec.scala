package ao.learn.mst.gen2

import org.specs2.mutable.SpecificationWithJUnit
import ao.learn.mst.gen2.game.{ExtensiveGameDecision, ExtensiveGame}
import ao.learn.mst.gen2.example.perfect.complete.PerfectCompleteGame
import ao.learn.mst.gen2.info.{InformationSet, InformationSetIndex, TraversingInformationSetIndexer}
import ao.learn.mst.gen2.cfr.{ChanceSampledCfrMinimizer, StrategyProfile}
import ao.learn.mst.gen2.example.slot.specific.k.MarkovBanditGame
import ao.learn.mst.gen2.example.rps.RockPaperScissorsGame
import ao.learn.mst.gen2.example.perfect.complete.decision.{PerfectCompleteDecisionAfterDownInfo, PerfectCompleteDecisionAfterUpInfo, PerfectCompleteDecisionFirstInfo}
import ao.learn.mst.gen2.example.imperfect.complete.ImperfectCompleteGame
import ao.learn.mst.gen2.example.imperfect.complete.decision.{ImperfectCompleteDecisionSecondInfo, ImperfectCompleteDecisionFirstInfo}
import ao.learn.mst.gen2.example.zerosum.ZeroSumGame
import ao.learn.mst.gen2.example.zerosum.info.{ZeroSumInfoBlue, ZeroSumInfoRed}
import ao.learn.mst.gen2.example.kuhn.adapt.{KuhnGameInfo, KuhnGame}
import ao.learn.mst.gen2.example.kuhn.card.KuhnCard
import ao.learn.mst.gen2.example.kuhn.card.KuhnCard.KuhnCard
import ao.learn.mst.gen2.example.kuhn.action.{KuhnPlayerAction, KuhnActionSequence}
import ao.learn.mst.gen2.example.kuhn.action.KuhnActionSequence.KuhnActionSequence
import ao.learn.mst.gen2.example.kuhn.action.KuhnPlayerAction.KuhnPlayerAction
import ao.learn.mst.gen2.example.slot.specific.bin.deterministic.DeterministicBinaryBanditGame


/**
 *
 */
class CfrMinimizerSpec
    extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  val epsilonProbability:Double =
//    0.01
    0.05


  //--------------------------------------------------------------------------------------------------------------------
  "Counterfactual Regret Minimization algorithm" should {
    val minimizer =
      new ChanceSampledCfrMinimizer()
//      new CfrMinimizer()

    def approximateOptimalStrategy(
        game: ExtensiveGame,
        iterations: Int
        ): (InformationSetIndex, StrategyProfile) =
    {
      val informationSetIndex =
        TraversingInformationSetIndexer.preciseIndex( game )

      val strategyProfile = trainStrategyProfile(
        game, informationSetIndex, iterations)

      (informationSetIndex, strategyProfile)
    }

    def trainStrategyProfile(
        game: ExtensiveGame,
        informationSetIndex: InformationSetIndex,
        iterations: Int
        ): StrategyProfile =
    {
      val strategyProfile =
        new StrategyProfile( informationSetIndex )

      for (i <- 1 to iterations) {
        minimizer.reduceRegret(
          game, informationSetIndex, strategyProfile)
      }

      strategyProfile
    }


    "Solve singleton information set problems" in {
      def approximateOptimalSingletonInformationSetStrategy(
           game: ExtensiveGame,
           iterations: Int): Seq[Double] =
      {
        approximateOptimalStrategy(game, iterations)._2.averageStrategy(
          game.treeRoot.asInstanceOf[ExtensiveGameDecision].informationSet,
          game.treeRoot.actions.size)
      }

      "Classical bandit setting" in {
        "Deterministic Binary Bandit" in {
          val optimalStrategy = approximateOptimalSingletonInformationSetStrategy(
            DeterministicBinaryBanditGame, 64)

          optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
        }

        "Markovian K-armed Bandit" in {
          val optimalStrategy = approximateOptimalSingletonInformationSetStrategy(
            new MarkovBanditGame(6), 16 * 1024)

          optimalStrategy.last must be greaterThan(1.0 - epsilonProbability)
        }
      }

      "Rock Packer Scissors" in {
        val optimalStrategy = approximateOptimalSingletonInformationSetStrategy(
          RockPaperScissorsGame, 1024)

        // (roughly) equal distribution
        optimalStrategy.min must be greaterThan(
          1.0/RockPaperScissorsGame.treeRoot.actions.size - epsilonProbability)
      }
    }

    "Solve sample problems from Wikipedia" in {
      "Perfect and complete information" in {
        val optimalStrategyProfile = approximateOptimalStrategy(
          PerfectCompleteGame, 256)._2

        val firstStrategy = optimalStrategyProfile.averageStrategy(
          PerfectCompleteDecisionFirstInfo, 2)

        firstStrategy(0) must be greaterThan(1.0 - epsilonProbability)

        val afterUpStrategy = optimalStrategyProfile.averageStrategy(
          PerfectCompleteDecisionAfterUpInfo, 2)

        afterUpStrategy(1) must be greaterThan(1.0 - epsilonProbability)

        val afterDownStrategy = optimalStrategyProfile.averageStrategy(
          PerfectCompleteDecisionAfterDownInfo, 2)

        afterDownStrategy(0) must be greaterThan(2.0/3 - epsilonProbability)
      }

      "Imperfect information" in {
        val optimalStrategyProfile = approximateOptimalStrategy(
          ImperfectCompleteGame, 128)._2

        val firstStrategy = optimalStrategyProfile.averageStrategy(
          ImperfectCompleteDecisionFirstInfo, 2)

        firstStrategy(1) must be greaterThan(1.0 - epsilonProbability)

        val secondStrategy = optimalStrategyProfile.averageStrategy(
          ImperfectCompleteDecisionSecondInfo, 2)

        secondStrategy(0) must be greaterThan(1.0 - epsilonProbability)
      }

//      "Incomplete information (non-zero-sum adjusted)" in {
//        // see adjustment in: IncompleteTerminal -> IncompleteTypeOne -> IncompleteActionDown
//
//        val optimalStrategyProfile = approximateOptimalStrategy(
//          IncompleteGame, 256)._2
//
//        val playerOneTypeOneStrategy = optimalStrategyProfile.averageStrategy(
//          IncompleteInfoPlayerOneTypeOne, 2)
//
//        playerOneTypeOneStrategy(IncompleteActionDown.index) must be greaterThan(0.99)
//
//        val playerOneTypeTwoStrategy = optimalStrategyProfile.averageStrategy(
//          IncompleteInfoPlayerOneTypeTwo, 2)
//
//        playerOneTypeTwoStrategy(IncompleteActionUp.index) must be greaterThan(0.99)
//
//        // playerTwoAfterUpStrategy is irrelevant(i.e. all actions produce same outcome)
//
//        val playerTwoAfterDownStrategy = optimalStrategyProfile.averageStrategy(
//          IncompleteInfoPlayerTwoAfterDown, 2)
//
//        playerTwoAfterDownStrategy(IncompleteActionUp.index) must be greaterThan(0.99)
//      }

      "Zero sum" in {
        val optimalStrategyProfile = approximateOptimalStrategy(
          ZeroSumGame, 64)._2

        val redStrategy = optimalStrategyProfile.averageStrategy(
          ZeroSumInfoRed, 2)

        redStrategy(0) must be greaterThan(4.0/7 - epsilonProbability)

        val blueStrategy = optimalStrategyProfile.averageStrategy(
          ZeroSumInfoBlue, 3)

        blueStrategy(0) must be lessThan epsilonProbability
        blueStrategy(1) must be greaterThan(4.0/7 - epsilonProbability)
      }
    }

    "Solve Kuhn Poker" in {
      val (informationSetIndex, optimalStrategyProfile) = approximateOptimalStrategy(
        KuhnGame, 14 * 1024)
//        KuhnGame, 16 * 1024)
//        KuhnGame, 24 * 1024)
//        KuhnGame, 512 * 1024)

      def kuhnStrategy(playerCard: KuhnCard, actionSequence: KuhnActionSequence, action:KuhnPlayerAction): Double =
        optimalStrategyProfile.averageStrategy(kuhnInfo(playerCard, actionSequence), 2)(action.id)

      def kuhnInfo(playerCard: KuhnCard, actionSequence: KuhnActionSequence): InformationSet =
        informationSetIndex.informationSets.find(_ match {
          case kuhnDecision: KuhnGameInfo =>
            kuhnDecision.contains(playerCard, actionSequence)
          case _ => false
        }).get

      "Strategies should not be dominated" in {
        val firstPlayerFirstActionWithQueenPass =
          kuhnStrategy(KuhnCard.Queen, KuhnActionSequence.Empty, KuhnPlayerAction.CheckFold)
        firstPlayerFirstActionWithQueenPass must be greaterThan(1.0 - epsilonProbability)

        val secondPlayerAfterPassWithQueenPass =
          kuhnStrategy(KuhnCard.Queen, KuhnActionSequence.Check, KuhnPlayerAction.CheckFold)
        secondPlayerAfterPassWithQueenPass must be greaterThan(1.0 - epsilonProbability)

        val secondPlayerAfterPassWithKingBet =
          kuhnStrategy(KuhnCard.King, KuhnActionSequence.Check, KuhnPlayerAction.CallRaise)
        secondPlayerAfterPassWithKingBet must be greaterThan(1.0 - epsilonProbability)
        val secondPlayerAfterBetWithKingBet =
          kuhnStrategy(KuhnCard.King, KuhnActionSequence.Raise, KuhnPlayerAction.CallRaise)
        secondPlayerAfterBetWithKingBet must be greaterThan(1.0 - epsilonProbability)

        val firstPlayerCheckRaiseWithJackPass =
          kuhnStrategy(KuhnCard.Jack, KuhnActionSequence.CheckRaise, KuhnPlayerAction.CheckFold)
        firstPlayerCheckRaiseWithJackPass must be greaterThan(1.0 - epsilonProbability)

        val secondPlayerAfterRaiseWithJackPass =
          kuhnStrategy(KuhnCard.Jack, KuhnActionSequence.Raise, KuhnPlayerAction.CheckFold)
        secondPlayerAfterRaiseWithJackPass must be greaterThan(1.0 - epsilonProbability)
      }

      "Second player should have unique optimal strategy" in {
        val secondPlayerAfterPassWithJackBet =
          kuhnStrategy(KuhnCard.Jack, KuhnActionSequence.Check, KuhnPlayerAction.CallRaise)
        secondPlayerAfterPassWithJackBet must be greaterThan(1.0/3 - epsilonProbability)
        secondPlayerAfterPassWithJackBet must be lessThan(1.0/3 + epsilonProbability)

        val secondPlayerAfterBetWithQueenBet =
          kuhnStrategy(KuhnCard.Queen, KuhnActionSequence.Raise, KuhnPlayerAction.CallRaise)
        secondPlayerAfterBetWithQueenBet must be greaterThan(1.0/3 - epsilonProbability)
        secondPlayerAfterBetWithQueenBet must be lessThan(1.0/3 + epsilonProbability)
      }

      "First player should have one of the many optimal strategies" in {
        val betWithJack =
          kuhnStrategy(KuhnCard.Jack, KuhnActionSequence.Empty, KuhnPlayerAction.CallRaise)

        val betWithQueenAfterPassBet =
          kuhnStrategy(KuhnCard.Queen, KuhnActionSequence.CheckRaise, KuhnPlayerAction.CallRaise)

        val betWithKing =
          kuhnStrategy(KuhnCard.King, KuhnActionSequence.Empty, KuhnPlayerAction.CallRaise)

        betWithJack must be lessThan(betWithKing / 3 + epsilonProbability)
        betWithJack must be greaterThan(betWithKing / 3 - epsilonProbability)

        betWithQueenAfterPassBet must be lessThan((1 + betWithKing) / 3 + epsilonProbability)
        betWithQueenAfterPassBet must be greaterThan((1 + betWithKing) / 3 - epsilonProbability)
      }
    }
  }
}