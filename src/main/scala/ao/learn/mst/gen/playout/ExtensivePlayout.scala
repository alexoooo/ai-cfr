package ao.learn.mst.gen.playout

import ao.learn.mst.gen.{ExtensiveGameNodeX, ExtensiveGameX}
import ao.learn.mst.gen.history.ExtensiveHistory
import util.Random
import ao.learn.mst.gen.player._
import ao.learn.mst.gen.act.{ExtensiveActionObservation, ExtensiveAction}

/**
 * Date: 15/11/11
 * Time: 12:02 AM
 */

class ExtensivePlayout(rand : Random)
{
  //--------------------------------------------------------------------------------------------------------------------
  def this() = this(new Random())


  //--------------------------------------------------------------------------------------------------------------------
  def play(
      extensiveGame : ExtensiveGameX,
      players       : Seq[ExtensivePlayer]) : ExtensiveOutcome =
  {
    assert( extensiveGame.rationalPlayerCount == 2 )

    play(extensiveGame.gameTreeRoot,
         players,
         Vector.empty)
  }

  private def play(
      node    : ExtensiveGameNodeX,
      players : Seq[ExtensivePlayer],
      history : Vector[ExtensiveActionObservation]) : ExtensiveOutcome =
  {
    node.payoff match {
      case Some( playerPayoffs ) =>
        ExtensiveOutcome( playerPayoffs, ExtensiveHistory(history) )

      case _ => {

        val nextToAct = node.nextToAct.get

        val nextAction = nextToAct match {
          case Chance => {
            val childProbabilities =
              node.childProbabilities.get.probabilities

            val weightedIndexes = (0 until node.childCount).zipWithIndex.map(e =>
              (e._2, childProbabilities(e._1) * rand.nextDouble()))

            val maxWeightIndex =
              weightedIndexes.sortBy(_._1).last._1

            ExtensiveAction( maxWeightIndex )
          }

          case Rational(ExtensivePlayerPosition( position )) => {
            players( position ).act( node )
          }
        }

        val actionObservation =
          ExtensiveActionObservation(
            nextToAct, nextAction)

        play(node.child( nextAction ).get,
             players,
             history :+ actionObservation)
      }
    }
  }
}