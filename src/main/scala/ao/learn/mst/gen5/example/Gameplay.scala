package ao.learn.mst.gen5.example

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensivePlayer, ExtensiveGame}
import ao.learn.mst.gen5.node.{Chance, Terminal, Decision}
import scala.util.Random
import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.learn.mst.gen5.cfr.ChanceSampledCfrMinimizer
import ao.learn.mst.gen5.example.abstraction.{SingleStateLosslessDecisionAbstractionBuilder, OpaqueAbstractionBuilder}
import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.example.simple.deterministic.DeterministicBinaryBanditGame
import ao.learn.mst.gen5.example.simple.bernoulli.BernoulliBinaryBanditGame
import ao.learn.mst.gen5.example.simple.uniform.UniformBinaryBanditGame
import ao.learn.mst.gen5.example.simple.gaussian.GaussianBinaryBanditGame
import ao.learn.mst.gen5.example.simple.rps.RockPaperScissorsGame
import ao.learn.mst.gen5.example.simple.rpsw.RockPaperScissorsWellGame


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
    RockPaperScissorsWellGame
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
      val meanOutcomes = outcomeSums.map(_ / gamesPlayed).toSeq
      println(s"Average outcomes: ${meanOutcomes.mkString("\t")}")
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
      new SingleStateLosslessDecisionAbstractionBuilder

    val abstraction : ExtensiveAbstraction[I, A] =
      abstractionBuilder.generate(game)

    val numberOfRounds : Int =
      100 * 1000

    val displayFrequency : Int =
      numberOfRounds / 1000

    val singletonInformationSet : I =
      game.node(game.initialState) match {
        case Decision(_, i, _) => i
        case _ => throw new UnsupportedOperationException
      }

    val decisionCount : Int =
      abstraction.actionCount(singletonInformationSet)

    for (i <- 1 to numberOfRounds) {
      solution.optimize(abstraction)
      if (i % displayFrequency == 0) {
        val strategy : ExtensiveStrategyProfile =
          solution.strategy

        val singletonBinaryProbabilities : Seq[Double] =
          strategy.actionProbabilityMass(0, decisionCount)

//        println(solution.strategy)
        println(singletonBinaryProbabilities.mkString("\t"))
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
