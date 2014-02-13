package ao.learn.mst.gen5.example

import ao.learn.mst.gen5.{ExtensivePlayer, ExtensiveAbstraction, ExtensiveGame}
import ao.learn.mst.gen5.example.abstraction.{SingleInfoLosslessDecisionAbstractionBuilder, AbstractionUtils, LosslessInfoLosslessDecisionAbstractionBuilder}
import ao.learn.mst.gen5.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.solve.{ExtensiveSolver, SolutionApproximation}
import ao.learn.mst.lib.CommonUtils
import ao.learn.mst.gen5.example.player.MixedStrategyPlayer
import scala.util.Random
import java.text.DecimalFormat
import ao.learn.mst.gen5.br._
import ao.learn.mst.gen5.cfr.{OutcomeSamplingCfrMinimizer, ProbingCfrMinimizer, ChanceSampledCfrMinimizer}
import ao.learn.mst.gen5.br.BestResponseProfile
import ao.learn.mst.gen5.br.BestResponsePlayer
import ao.learn.mst.gen5.abstraction.InfoPerDecisionSetAbstraction

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
    iterations      : Int,
    averageStrategy : Boolean = false)
    : SimpleGameSolution[S, I, A] =
  {
    val losslessAbstraction : ExtensiveAbstraction[I, A] =
      //LosslessInfoLosslessDecisionAbstractionBuilder.generate(game)
      new InfoPerDecisionSetAbstraction()

    val strategy : ExtensiveStrategyProfile =
      solve(game, averageStrategy, losslessAbstraction, iterations)

    val response : BestResponseProfile[I, A] =
      BestResponseFinder.bestResponseProfile(
        game, losslessAbstraction, strategy)

//      GameValueFinder.bestResponseProfile(
//        game, abstraction, strategy)

    SimpleGameSolution(
      game,
      iterations,
      averageStrategy,
      losslessAbstraction,
      strategy,
      response)
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def solve[S, I, A](
      game            : ExtensiveGame[S, I, A],
      averageStrategy : Boolean,
      abstraction     : ExtensiveAbstraction[I, A],
      iterations      : Int)
    : ExtensiveStrategyProfile =
  {
    val solver : ExtensiveSolver[S, I, A] =
//      new ChanceSampledCfrMinimizer[S, I, A](averageStrategy)
      new OutcomeSamplingCfrMinimizer[S, I, A](averageStrategy)
//      new ProbingCfrMinimizer[S, I, A](averageStrategy)

    val solution : SolutionApproximation[I, A] =
      solver.initialSolution(game)

    val informationSets : Set[I] =
      AbstractionUtils.informationSets(game)

    val infoDisplayOrder : Seq[I] =
      informationSets.toSeq//.sortBy(_.toString)

    def displayStrategy(round : Long) : Unit = {
//      CommonUtils.displayDelimiter()
//      println(s"round: ${countFormat.format(round)}")
//
      for (i <- infoDisplayOrder) {
        val infoIndex = abstraction.informationSetIndex(i)
        val probabilities = solution.strategy.actionProbabilityMass(infoIndex)
        println(s"$i\t${CommonUtils.displayProbabilities(probabilities)}")
      }
//
//      ResponseTreeTraverser.traverseResponseTreeLeaves(
//        game, abstraction, solution.strategy, 0
//      ).foreach(println)

      val gameValue : Seq[Double] =
        BestResponseFinder.bestResponseProfile(
          game, abstraction, solution.strategy
        ).bestResponses.map(_.value)

      println(s"${countFormat.format(round)}\t${CommonUtils.formatGameValue(gameValue)}")
    }

    val displayFrequency : Int =
      math.max(1, iterations / 1000)

    for (i <- 1 to iterations) {
      solution.optimize(abstraction)
      if (i % displayFrequency == 0) {
        displayStrategy(i)
      }
    }

    val strategy : ExtensiveStrategyProfile =
      solution.strategy

    strategy
  }
}



case class SimpleGameSolution[S, I, A](
  game            : ExtensiveGame[S, I, A],
  iterations      : Int,
  averageStrategy : Boolean,
  abstraction     : ExtensiveAbstraction[I, A],
  strategy        : ExtensiveStrategyProfile,
  response        : BestResponseProfile[I, A])
{
  def strategyPlayers : Seq[ExtensivePlayer[I, A]] = {
    val playerPlayers : ExtensivePlayer[I, A] =
      new MixedStrategyPlayer[I, A](
        strategy, abstraction, new Random)

    Seq.fill(game.playerCount)(playerPlayers)
  }

  def responsePlayer(index : Int) : ExtensivePlayer[I, A] =
    BestResponsePlayer(response)
}