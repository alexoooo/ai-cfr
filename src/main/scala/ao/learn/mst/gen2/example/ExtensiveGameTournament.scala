package ao.learn.mst.gen2.example

import ao.learn.mst.gen2.game._
import ao.learn.mst.gen2.player.ExtensiveGamePlayer
import ao.learn.mst.gen2.player.impl.{RandomExtensiveGamePlayer, StrategyProfileExtensiveGamePlayer}
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.player.model.FiniteAction
import scala.util.Random
import scala.annotation.tailrec
import scala.Predef._
import ao.learn.mst.gen2.player.model.DeliberatePlayer
import ao.learn.mst.gen2.example.ocp.adapt.OcpGame
import ao.learn.mst.gen2.info.{TraversingInformationSetIndexer, SingleInformationSetIndexer, InformationSetIndex}

/**
 * 25/04/13 7:58 PM
 */
object ExtensiveGameTournament
  extends App
{
  //--------------------------------------------------------------------------------------------------------------------
  val game : ExtensiveGame =
//    KuhnGame
    OcpGame

  val players:Seq[ExtensiveGamePlayer] =
    computePlayers(
      game,
      Map(
//        0 -> SingleInformationSetIndexer.single( game ),
//        1 -> TraversingInformationSetIndexer.preciseIndex( game )
        1 -> SingleInformationSetIndexer.single( game ),
        0 -> TraversingInformationSetIndexer.preciseIndex( game )
      ),
      32 * 1024)

//    (1 to game.rationalPlayerCount).map(_ =>
//      RandomExtensiveGamePlayer)

  val tournamentIterations =
//    1024
//    32 * 1024
    1024 * 1024

  iterateTournament(
    game,
    players,
    tournamentIterations)


  //--------------------------------------------------------------------------------------------------------------------
  def computePlayers(
        game : ExtensiveGame,
        informationSets : Map[Int, InformationSetIndex],
        iterations:Int)
        : Seq[ExtensiveGamePlayer] =
  {
    if (informationSets.isEmpty) {
      return (0 until game.rationalPlayerCount).map(_ => RandomExtensiveGamePlayer)
    }

    (0 until game.rationalPlayerCount).map(
      (playerIndex: Int) =>
        if (! informationSets.contains(playerIndex))
          RandomExtensiveGamePlayer
        else {
          new StrategyProfileExtensiveGamePlayer(
            ExtensiveGameSolver.computeStrategy(
              game,
              iterations,
              informationSets(playerIndex)))
        }
    )
  }


  //--------------------------------------------------------------------------------------------------------------------
  def iterateTournament(
      game : ExtensiveGame,
      players : Seq[ExtensiveGamePlayer],
      iterations: Int)
  {
    println("players: \n" + players.mkString("\n") + "\n")

    var outcomeSums =
      Map[DeliberatePlayer, Double]()

    for (iteration <- 1 to iterations) {
      val outcome:ExpectedValue =
        determineOutcome(game, players)

      outcomeSums =
        addOutcome(outcomeSums, outcome)

      displayOutcomesIfNeeded(iteration, iterations, outcomeSums)
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  def addOutcome(
      currentOutcomeSums:Map[DeliberatePlayer, Double],
      outcome:ExpectedValue)
        :Map[DeliberatePlayer, Double] =
  {
    var nextOutcomeSums = currentOutcomeSums
    for ((player, reward) <- outcome.outcomes) {
      val currentSum:Double =
        currentOutcomeSums.getOrElse(player, 0.0)

      val nextSum:Double =
        currentSum + reward

      nextOutcomeSums +=
        player -> nextSum
    }
    nextOutcomeSums
  }


  //--------------------------------------------------------------------------------------------------------------------
  def displayOutcomesIfNeeded(iteration:Int, iterations:Int, outcomeSums:Map[DeliberatePlayer, Double]) {
    val period:Int = iterations / 1000
    if (period == 0 || (iteration - 1) % period == 0) {
      displayOutcomes(iteration, outcomeSums)
    }
  }
  def displayOutcomes(iteration:Int, outcomeSums:Map[DeliberatePlayer, Double]) {
    val averageOutcomes = outcomeSums.transform(
      (_, outcomeSum: Double) => outcomeSum / iteration)

    println(averageOutcomes)
  }


  //--------------------------------------------------------------------------------------------------------------------
  def determineOutcome(
      game : ExtensiveGame,
      players : Seq[ExtensiveGamePlayer]):ExpectedValue =
    determineOutcome(game.treeRoot, players)

  @tailrec
  def determineOutcome(
      node : ExtensiveGameNode,
      players : Seq[ExtensiveGamePlayer]):ExpectedValue =
  {
    node match {
      case terminal : ExtensiveGameTerminal =>
        terminal.payoff

      case nonTerminal : ExtensiveGameNonTerminal => {
        val action:FiniteAction = nonTerminal match {
          case chance : ExtensiveGameChance => {
            val actionToProbability:Map[FiniteAction, Double] =
              chance.probabilityMass.actionProbabilities

            val actionToPosterior:Map[FiniteAction, Double] =
              actionToProbability.transform(
                (_, probability: Double) =>
                  Random.nextDouble() * probability)

            actionToPosterior.maxBy(_._2)._1
          }

          case decision : ExtensiveGameDecision => {
            val player =
              players(decision.player.index)

            player.selectAction(decision)
          }
        }

        val child =
          nonTerminal.child(action)

        determineOutcome(child, players)
      }
    }
  }
}
