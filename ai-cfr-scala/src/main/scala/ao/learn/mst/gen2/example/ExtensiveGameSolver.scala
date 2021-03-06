package ao.learn.mst.gen2.example

import xml.PrettyPrinter
import ao.learn.mst.gen2.info.{TraversingInformationSetIndexer, InformationSetIndex, SingleInformationSetIndexer}
import ao.learn.mst.gen2.cfr._
import ao.learn.mst.gen2.game._
import ao.learn.mst.gen2.example.kuhn.adapt.KuhnGame
import scala.util.Random

//import org.joda.time.{Duration, LocalTime, DateTime}


/**
 * User: ao
 */

object ExtensiveGameSolver
    extends App
{
  //--------------------------------------------------------------------------------------------------------------------
  val formatter = new PrettyPrinter(120, 4)


  //--------------------------------------------------------------------------------------------------------------------
  val game : ExtensiveGame =
//    DeterministicBinaryBanditGame
//    new MarkovBanditGame(16)
//    RockPaperScissorsGame
//    PerfectCompleteGame
//    ImperfectCompleteGame
//    IncompleteGame // does not randomize?
//    ZeroSumGame
    KuhnGame
//    OcpGame

  val extensiveGameRoot =
    game.treeRoot

  println("rational player count = " +
    game.rationalPlayerCount)

  //-------------------------------------------------------
  val extensiveGameViewRoot =
    ExtensiveGameDisplay.displayExtensiveGameNode(
      extensiveGameRoot)

  val showFullGame = false
  val showGameViews = true

  if (showFullGame)
  {
    println("Full extensive game")
    println(formatter.format(extensiveGameViewRoot))
  }
  else
  {
    println("Game name: " + game)
  }


  //-------------------------------------------------------
  if (showGameViews)
  {
//      val firstPlayerView =
//        PlayerViewBuilder.expand(
//          game, RationalPlayer(0))
//
//      println("First player's view")
//      println(formatter.format(
//        displayPlayerViewNode( firstPlayerView )))
//
//      val secondPlayerView =
//        PlayerViewBuilder.expand(
//          game, RationalPlayer(1))
//
//      println("Second player's view")
//      println(formatter.format(
//        displayPlayerViewNode( secondPlayerView )))
  }



  //--------------------------------------------------------------------------------------------------------------------
  val equilibriumApproximationIterations =
    1000 * 1000

  println("\n\n\nCalculating Equilibrium " +
    equilibriumApproximationIterations)

  val strategyProfile =
    computeStrategy(game, equilibriumApproximationIterations)

  println("\n\n\n\n\n")
  println( strategyProfile )

//  println("\nFirst player's view")
//  println(formatter.format(
//    displayPlayerViewNode( firstPlayerView )))
//
//  println("\nSecond player's view")
//  println(formatter.format(
//    displayPlayerViewNode( secondPlayerView )))


  //--------------------------------------------------------------------------------------------------------------------
  def computeStrategy(
      game:ExtensiveGame,
      iterations:Int):StrategyProfile =
  {
    val informationSetIndex =
      TraversingInformationSetIndexer.preciseIndex( game )
//          SingleInformationSetIndexer.single( game )

    computeStrategy(game, iterations, informationSetIndex)
  }

  def computeStrategy(
      game:ExtensiveGame,
      iterations:Int,
      informationSetIndex:InformationSetIndex)
      : StrategyProfile =
  {
    val strategyProfile =
      new StrategyProfile( informationSetIndex )

    println( strategyProfile )

    val minimizer =
    //    new CfrMinimizer()
      new ChanceSampledCfrMinimizer(
        new Random(0)
//        new Random(0) {
//          override def nextDouble(): Double =
//            0.0
//        }
      )

    for (i <- 1 to iterations) {
      minimizer.reduceRegret(
        game, informationSetIndex, strategyProfile)
      //    println( strategyProfile )

      if (i % math.max(1, equilibriumApproximationIterations / 1000) == 0) {
        println(i)
        println(strategyProfile)
      }
    }

    strategyProfile
  }
}
