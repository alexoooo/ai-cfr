package ao.learn.mst.gen5.example

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensivePlayer, ExtensiveGame}
import ao.learn.mst.gen5.node.{Chance, Terminal, Decision}
import scala.util.Random
import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.learn.mst.gen5.cfr.ChanceSampledCfrMinimizer
import ao.learn.mst.gen5.example.abstraction.{AbstractionUtils, LosslessInfoLosslessDecisionAbstractionBuilder, SingleInfoLosslessDecisionAbstractionBuilder, OpaqueAbstractionBuilder}
import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.example.bandit.deterministic.DeterministicBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.bernoulli.BernoulliBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.uniform.UniformBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.gaussian.GaussianBinaryBanditGame
import ao.learn.mst.gen5.example.bandit.rps.RockPaperScissorsGame
import ao.learn.mst.gen5.example.bandit.rpsw.RockPaperScissorsWellGame
import ao.learn.mst.gen5.example.matrix.MatrixGames
import java.text.DecimalFormat
import ao.learn.mst.gen5.example.perfect.complete.PerfectCompleteGame
import ao.learn.mst.gen5.example.imperfect.ImperfectGame
import ao.learn.mst.gen5.example.monty.{BasicMontyHallGame, MontyHallGame}
import ao.learn.mst.lib.CommonUtils
import com.google.common.base.Strings
import ao.learn.mst.gen5.example.sig.SignalingGame


object Gameplay extends App
{
  //--------------------------------------------------------------------------------------------------------------------
  implicit val sourceOfRandomness : Random =
    new Random()


  //--------------------------------------------------------------------------------------------------------------------
  val resultFormat = new DecimalFormat("0.0000")
  val countFormat  = new DecimalFormat(",000")


  //--------------------------------------------------------------------------------------------------------------------
  val solutionIterationCount : Int =
    1000

  val averageStrategy : Boolean =
//    false
    true

  def solver[S, I, A]() : ExtensiveSolver[S, I, A] =
    new ChanceSampledCfrMinimizer[S, I, A](averageStrategy)


  //--------------------------------------------------------------------------------------------------------------------
  play(
//    DeterministicBinaryBanditGame.plusMinusOne
//    BernoulliBinaryBanditGame.withAdvantageForTrue(0.01)
//    UniformBinaryBanditGame.withAdvantageForTrue(0.01)
//    GaussianBinaryBanditGame.withAdvantageForTrue(0.01)
//    RockPaperScissorsGame
//    RockPaperScissorsWellGame

//    MatrixGames.matchingPennies
//    MatrixGames.deadlock
//    MatrixGames.prisonersDilemma
//    MatrixGames.zeroSum
//    MatrixGames.battleOfTheSexes
//    MatrixGames.burningMoney

//    PerfectCompleteGame
//    ImperfectGame
//    SignalingGame
//    BasicMontyHallGame

    MontyHallGame
  )


  //--------------------------------------------------------------------------------------------------------------------
  def play[S, I, A](game : ExtensiveGame[S, I, A]) =
  {
    val players : Seq[ExtensivePlayer[I, A]] =
      solve(game)
//      Seq.fill(game.playerCount)(new RandomPlayer[I, A](new Random))

    displayOutcome(game, players)
  }


  //--------------------------------------------------------------------------------------------------------------------
  def solve[S, I, A](game : ExtensiveGame[S, I, A]) : Seq[ExtensivePlayer[I, A]] =
  {
    val solution : SolutionApproximation[I, A] =
      solver().initialSolution(game)

    val abstractionBuilder : OpaqueAbstractionBuilder =
      LosslessInfoLosslessDecisionAbstractionBuilder
//      SingleStateLosslessDecisionAbstractionBuilder

    val abstraction : ExtensiveAbstraction[I, A] =
      abstractionBuilder.generate(game)

    val informationSets : Set[I] =
      AbstractionUtils.informationSets(game)

    val infoDisplayOrder : Seq[I] =
      informationSets.toSeq.sortBy(_.toString)

    def displayStrategy(round : Long) : Unit = {
      CommonUtils.displayDelimiter()
      println(s"round: ${countFormat.format(round)}")
      val strategy = solution.strategy
      for (i <- infoDisplayOrder) {
        val infoIndex = abstraction.informationSetIndex(i)
        val probabilities = strategy.actionProbabilityMass(infoIndex)
        println(s"$i\t${CommonUtils.displayProbabilities(probabilities)}")
      }
    }

    val numberOfRounds : Int =
      solutionIterationCount

    val displayFrequency : Int =
      math.max(1, numberOfRounds / 1000)

    for (i <- 1 to numberOfRounds) {
      solution.optimize(abstraction)
      if (i % displayFrequency == 0) {
        displayStrategy(i)
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
  def displayOutcome[S, I, A](
      game : ExtensiveGame[S, I, A],
      players : Seq[ExtensivePlayer[I, A]]) : Unit =
  {
    val outcomeSums =
      new Array[Double](game.playerCount)

    val outcomeCount : Int =
      100 * 1000

    val displayInterval : Int =
      outcomeCount / 10

    CommonUtils.displayDelimiter()

    for (i <- 1 to outcomeCount) {
      val outcome : Seq[Double] =
        playout(game, players)

      for (p <- 0 until game.playerCount) {
        outcomeSums(p) += outcome(p)
      }

      if (i % displayInterval == 0) {
        displayMeanResults(outcomeSums, i)
      }
    }

    displayMeanResults(outcomeSums, outcomeCount)
  }

  def displayMeanResults(outcomeSums : Seq[Double], gamesPlayed: Int) : Unit = {
    val meanOutcomes : Seq[Double] =
      outcomeSums.map(_ / gamesPlayed).toSeq

    val formattedMeanOutcomes : Seq[String] =
      meanOutcomes.map(resultFormat.format)

    println(s"Average results: ${formattedMeanOutcomes.mkString("\t")}")
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
