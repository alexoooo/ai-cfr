package ao.learn.mst.gen5.example

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensivePlayer, ExtensiveGame}
import ao.learn.mst.gen5.example.simple.DeterministicBinaryBanditGame
import ao.learn.mst.gen5.node.{Chance, Terminal, Decision}
import scala.util.Random
import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.learn.mst.gen5.cfr.ChanceSampledCfrMinimizer
import ao.learn.mst.gen5.example.abstraction.{SingleStateLosslessDecisionAbstractionBuilder, OpaqueAbstractionBuilder}
import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile


object Gameplay extends App
{
  //--------------------------------------------------------------------------------------------------------------------
  play(DeterministicBinaryBanditGame)


  //--------------------------------------------------------------------------------------------------------------------
  def play[S, I, A](game : ExtensiveGame[S, I, A]) =
  {
    val players : Seq[ExtensivePlayer[I, A]] =
      solve(game)
//      Seq.fill(game.playerCount)(new RandomPlayer[I, A](new Random))

    val outcomeSums =
      new Array[Double](game.playerCount)

    var outcomeCount : Int = 0

    for (i <- 1 to 100) {
      val outcome : Seq[Double] =
        playout(game, players)

      println(outcome)

      for (p <- 0 until game.playerCount) {
        outcomeSums(p) += outcome(p)
      }

      outcomeCount += 1
    }

    val meanOutcomes : Seq[Double] =
      outcomeSums.map(_ / outcomeCount)

    println(s"Average outcomes: $meanOutcomes")
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

    for (i <- 1 to 1000 * 1000) {
      solution.optimize(abstraction)
      if (i % 1000 == 0) {
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
