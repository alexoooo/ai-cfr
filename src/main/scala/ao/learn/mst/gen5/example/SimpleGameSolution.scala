package ao.learn.mst.gen5.example

import ao.learn.mst.gen5.{ExtensivePlayer, ExtensiveAbstraction, ExtensiveGame}
import ao.learn.mst.gen5.example.abstraction.{SingleInfoLosslessDecisionAbstractionBuilder, AbstractionUtils}
import ao.learn.mst.gen5.solve.{ExtensiveSolver, SolutionApproximation}
import ao.learn.mst.lib.DisplayUtils
import ao.learn.mst.gen5.example.player.MixedStrategyPlayer
import scala.util.Random
import java.text.DecimalFormat
import ao.learn.mst.gen5.br._
import ao.learn.mst.gen5.cfr.{OutcomeSamplingCfrMinimizer, ProbingCfrMinimizer, ChanceSampledCfrMinimizer}
import ao.learn.mst.gen5.br.BestResponseProfile
import ao.learn.mst.gen5.br.BestResponsePlayer
import com.google.common.base.Stopwatch
import ao.learn.mst.gen5.state.MixedStrategy
import ao.learn.mst.gen5.abstraction.LosslessInfoLosslessDecisionAbstractionBuilder
import ao.learn.mst.gen5.solve2.{RegretMinimizer, RegretSampler}

/**
 *
 */
object SimpleGameSolution
{
  //--------------------------------------------------------------------------------------------------------------------
  val countFormat  = new DecimalFormat(",000")


  //--------------------------------------------------------------------------------------------------------------------
  def forGame[S, I, A](
      game            : ExtensiveGame[S, I, A],
      solver          : RegretSampler[S, I, A],
      averageStrategy : Boolean,
      iterations      : Int,
      display         : Boolean = false
      ): SimpleGameSolution[S, I, A] =
  {
    val losslessAbstraction: ExtensiveAbstraction[I, A] =
      LosslessInfoLosslessDecisionAbstractionBuilder.generate(game)
//      SingleInfoLosslessDecisionAbstractionBuilder.generate(game)

    val timer = Stopwatch.createStarted()
    val strategy: MixedStrategy =
      solve(game, losslessAbstraction, solver, averageStrategy, iterations, display)
    println(s"Took: $timer")

//    val response: BestResponseProfile[I, A] =
//      BestResponseFinder.bestResponseProfile(
//        game, losslessAbstraction, strategy)

    SimpleGameSolution(
      game,
      iterations,
      losslessAbstraction,
      strategy)
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def solve[S, I, A](
      game            : ExtensiveGame[S, I, A],
      abstraction     : ExtensiveAbstraction[I, A],
      solver          : RegretSampler[S, I, A],
      averageStrategy : Boolean,
      iterations      : Int,
      display         : Boolean)
      : MixedStrategy =
  {
//    val solver : ExtensiveSolver[S, I, A] =
//      new ChanceSampledCfrMinimizer[S, I, A](averageStrategy)
//      new OutcomeSamplingCfrMinimizer[S, I, A](averageStrategy)
//      new ProbingCfrMinimizer[S, I, A](averageStrategy)

    val minimizer =
      new RegretMinimizer(game, abstraction, averageStrategy = averageStrategy)

    val informationSets: Set[I] =
      AbstractionUtils.informationSets(game)

    val infoDisplayOrder: Seq[I] =
      informationSets.toSeq//.sortBy(_.toString)

    def displayStrategy(round: Long) : Unit = {
//      CommonUtils.displayDelimiter()
      println(s"\nround: ${countFormat.format(round)}")

//      println()
      for (i <- infoDisplayOrder) {
        val infoIndex = abstraction.informationSetIndex(i)
        val actionCount = abstraction.actionCount(i)
        val probabilities = minimizer.strategyView.probabilities(infoIndex, actionCount)
        println(s"$i\t${DisplayUtils.displayProbabilities(probabilities)}")
      }

//      val gameValue: Seq[Double] =
//        BestResponseFinder.bestResponseProfile(
//          game, abstraction, minimizer.strategyView
//        ).bestResponses.map(_.value)
//
//      println(s"${countFormat.format(round)}\t${DisplayUtils.formatGameValue(gameValue)}")
    }

    val displayFrequency: Int =
      math.max(1, iterations / 1000)

    for (i <- 1 to iterations) {
      minimizer.iterate(solver)
      if (i % displayFrequency == 0 && display) {
        displayStrategy(i)
      }
    }

    if (display) {
      displayStrategy(iterations)
    }

    val strategy: MixedStrategy =
      minimizer.strategyView

    strategy
  }
}



case class SimpleGameSolution[S, I, A](
  game        : ExtensiveGame[S, I, A],
  iterations  : Int,
  abstraction : ExtensiveAbstraction[I, A],
  strategy    : MixedStrategy/*,
  response    : BestResponseProfile[I, A]*/)
{
  def strategyPlayers : Seq[ExtensivePlayer[I, A]] = {
    val playerPlayers : ExtensivePlayer[I, A] =
      new MixedStrategyPlayer[I, A](
        strategy, abstraction, new Random)

    Seq.fill(game.playerCount)(playerPlayers)
  }

//  def responsePlayer(index : Int) : ExtensivePlayer[I, A] =
//    BestResponsePlayer(response)
}