package ao.learn.mst.gen5.example

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensivePlayer, ExtensiveGame}
import ao.learn.mst.gen5.node.{Chance, Terminal, Decision}
import scala.util.Random
import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.learn.mst.gen5.cfr.ChanceSampledCfrMinimizer
import ao.learn.mst.gen5.example.abstraction.{LosslessInfoLosslessDecisionAbstractionBuilder, SingleInfoLosslessDecisionAbstractionBuilder, OpaqueAbstractionBuilder}
import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.example.bandit.deterministic.DeterministicBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.bernoulli.BernoulliBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.uniform.UniformBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.gaussian.GaussianBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.rps.RockPaperScissorsGame
import ao.learn.mst.gen5.example.bandit.rpsw.RockPaperScissorsWellGame
import ao.learn.mst.gen5.example.bandit.sig.SignalingGame
import ao.learn.mst.gen5.example.matrix.MatrixGames
import java.text.DecimalFormat
import ao.learn.mst.gen5.example.perfect.complete.PerfectCompleteGame


object Gameplay extends App
{
  //--------------------------------------------------------------------------------------------------------------------
  implicit val sourceOfRandomness : Random =
    new Random()

  play(
//    DeterministicBinaryBanditGame.plusMinusOne
//    BernoulliBinaryBanditGame.withAdvantageForTrue(0.01)
//    UniformBinaryBanditGame.withAdvantageForTrue(0.01)
//    GaussianBinaryBanditGame.withAdvantageForTrue(0.01)
//    RockPaperScissorsGame
//    RockPaperScissorsWellGame
//    SignalingGame

//    MatrixGames.matchingPennies
//    MatrixGames.deadlock
//    MatrixGames.prisonersDilemma
//    MatrixGames.zeroSum
    PerfectCompleteGame
  )


  //--------------------------------------------------------------------------------------------------------------------
  def play[S, I, A](game : ExtensiveGame[S, I, A]) =
  {
    val players : Seq[ExtensivePlayer[I, A]] =
      solve(game)
//      Seq.fill(game.playerCount)(new RandomPlayer[I, A](new Random))

    val outcomeSums =
      new Array[Double](game.playerCount)

    val outcomeCount : Int =
      100 * 1000

    val displayInterval : Int =
      outcomeCount / 10

    def displayMeanOutcomes(gamesPlayed: Int) : Unit = {
      val meanOutcomes : Seq[Double] =
        outcomeSums.map(_ / gamesPlayed).toSeq

      val formatter = new DecimalFormat("0.0000")
      val formattedMeanOutcomes : Seq[String] =
        meanOutcomes.map(formatter.format)

      println(s"Average outcomes: ${formattedMeanOutcomes.mkString("\t")}")
    }

    for (i <- 1 to outcomeCount) {
      val outcome : Seq[Double] =
        playout(game, players)

      for (p <- 0 until game.playerCount) {
        outcomeSums(p) += outcome(p)
      }

      if (i % displayInterval == 0) {
        displayMeanOutcomes(i)
      }
    }

    displayMeanOutcomes(outcomeCount)
  }


  def solve[S, I, A](game : ExtensiveGame[S, I, A]) : Seq[ExtensivePlayer[I, A]] =
  {
    val solver : ExtensiveSolver[S, I, A] =
      new ChanceSampledCfrMinimizer[S, I, A]

    val solution : SolutionApproximation[I, A] =
      solver.initialSolution(game)

    val abstractionBuilder : OpaqueAbstractionBuilder =
      LosslessInfoLosslessDecisionAbstractionBuilder
//      SingleStateLosslessDecisionAbstractionBuilder

    val abstraction : ExtensiveAbstraction[I, A] =
      abstractionBuilder.generate(game)

    val numberOfRounds : Int =
      100 * 1000

    val displayFrequency : Int =
      math.max(1, numberOfRounds / 1000)

    for (i <- 1 to numberOfRounds) {
      solution.optimize(abstraction)
      if (i % displayFrequency == 0) {
        println(solution.strategy)
      }
    }

    val strategy : ExtensiveStrategyProfile =
      solution.strategy

    val player =
      new MixedStrategyPlayer[I, A](
        strategy, abstraction, new Random)

    Seq.fill(game.playerCount)(player)
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
