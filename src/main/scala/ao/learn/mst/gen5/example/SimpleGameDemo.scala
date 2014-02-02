package ao.learn.mst.gen5.example

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensivePlayer, ExtensiveGame}
import ao.learn.mst.gen5.node.{Chance, Terminal, Decision}
import scala.util.Random
import ao.learn.mst.gen5.solve.ExtensiveSolver
import ao.learn.mst.gen5.cfr.{OutcomeSamplingCfrMinimizer, ExternalSamplingCfrMinimizer, ChanceSampledCfrMinimizer}
import ao.learn.mst.gen5.example.abstraction.{AbstractionUtils, SingleInfoLosslessDecisionAbstractionBuilder, OpaqueAbstractionBuilder}
import ao.learn.mst.gen5.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.example.bandit.deterministic.DeterministicBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.bernoulli.BernoulliBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.uniform.UniformBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.gaussian.GaussianBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.rpsw.RockPaperScissorsWellGame
import ao.learn.mst.gen5.example.matrix.MatrixGames
import java.text.DecimalFormat
import ao.learn.mst.gen5.example.perfect.complete.PerfectCompleteGame
import ao.learn.mst.gen5.example.imperfect.ImperfectGame
import ao.learn.mst.gen5.example.monty.BasicMontyHallGame
import ao.learn.mst.lib.CommonUtils
import ao.learn.mst.gen5.br.ResponseTreeTraverser


object SimpleGameDemo extends App
{
  //--------------------------------------------------------------------------------------------------------------------
  implicit val sourceOfRandomness : Random =
    new Random()


  //--------------------------------------------------------------------------------------------------------------------
  val solutionIterationCount : Int =
    100 * 1000

  val averageStrategy : Boolean =
//    false
    true

  def solver[S, I, A]() : ExtensiveSolver[S, I, A] =
//    new ChanceSampledCfrMinimizer[S, I, A](averageStrategy)
//    new ExternalSamplingCfrMinimizer[S, I, A](averageStrategy)
    new OutcomeSamplingCfrMinimizer[S, I, A](averageStrategy)


  //--------------------------------------------------------------------------------------------------------------------
  play(
//    DeterministicBinaryBanditGame.plusMinusOne
//    BernoulliBinaryBanditGame.withAdvantageForTrue(0.01)
//    UniformBinaryBanditGame.withAdvantageForTrue(0.01)
//    GaussianBinaryBanditGame.withAdvantageForTrue(0.01)
//    RockPaperScissorsGame
//    RockPaperScissorsWellGame

    MatrixGames.matchingPennies
//    MatrixGames.deadlock
//    MatrixGames.prisonersDilemma
//    MatrixGames.zeroSum
//    MatrixGames.battleOfTheSexes
//    MatrixGames.stagHunt
//    MatrixGames.choosingSides
//    MatrixGames.pureCoordination
//    MatrixGames.chicken

//    PerfectCompleteGame
//    ImperfectGame
//    SignalingGame
//    BasicMontyHallGame
//    MontyHallGame
//    BurningGame
  )


  //--------------------------------------------------------------------------------------------------------------------
  def play[S, I, A](game : ExtensiveGame[S, I, A]) : Unit =
  {
    val solution : SimpleGameSolution[S, I, A] =
      SimpleGameSolution.forGame(game, solutionIterationCount, averageStrategy)

    val strategyPlayers : Seq[ExtensivePlayer[I, A]] =
      solution.strategyPlayers

    displayGameValue("\n\nSolution", game, strategyPlayers)

    val responseValues : Seq[Double] =
      solution.response.bestResponses.map(_.value)

    println(s"\nResponse values: ${CommonUtils.formatGameValue(responseValues)}")

    for (player <- 0 until game.playerCount) {
      val respondedStrategyPlayers : Seq[ExtensivePlayer[I, A]] =
        strategyPlayers.updated(player, solution.responsePlayer(player))

      displayGameValue("Solution", game, respondedStrategyPlayers)
    }
  }

  def displayGameValue[S, I, A](
      prefix: String, game : ExtensiveGame[S, I, A], players : Seq[ExtensivePlayer[I, A]]): Unit = {
    val gameValues = computeGameValues(game, players)
    println(s"$prefix: ${CommonUtils.formatGameValue(gameValues)}")
  }



  //--------------------------------------------------------------------------------------------------------------------
  def computeGameValues[S, I, A](
      game : ExtensiveGame[S, I, A],
      players : Seq[ExtensivePlayer[I, A]]) : Seq[Double] =
  {
    val outcomeSums =
      new Array[Double](game.playerCount)

    val outcomeCount : Int =
      10 * 1000

    for (i <- 1 to outcomeCount) {
      val outcome : Seq[Double] =
        playout(game, players)

      for (p <- 0 until game.playerCount) {
        outcomeSums(p) += outcome(p)
      }
    }

    val meanOutcomes : Seq[Double] =
      outcomeSums.map(_ / outcomeCount).toSeq

    meanOutcomes
  }


  //--------------------------------------------------------------------------------------------------------------------
  def playout[S, I, A](
    game    : ExtensiveGame[S, I, A],
    players : Seq[ExtensivePlayer[I, A]]
    ) : Seq[Double] =
  {
    val root =
      game.initialState

    playout(root, game, players)
  }

  def playout[S, I, A](
      state   : S,
      game    : ExtensiveGame[S, I, A],
      players : Seq[ExtensivePlayer[I, A]]
      ) : Seq[Double] =
  {
    val node = game.node(state)

    node match {
      case Terminal(payoffs) =>
        payoffs

      case Chance(outcomes) => {
        val sampledOutcome =
          outcomes.maxBy(_.probability * math.random).action

        val sampledState =
          game.transition(state, sampledOutcome)

        playout(sampledState, game, players)
      }

      case Decision(
          nextToAct, informationSet, choices) =>
      {
        val player =
          players(nextToAct.index)

        val choice =
          player.act(informationSet, choices)

        val chosenState =
          game.transition(state, choice)

        playout(chosenState, game, players)
      }
    }
  }
}
