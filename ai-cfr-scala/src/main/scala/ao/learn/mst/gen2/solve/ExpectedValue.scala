package ao.learn.mst.gen2.solve

import scala.Predef._
import ao.learn.mst.gen2.player.model.DeliberatePlayer


//----------------------------------------------------------------------------------------------------------------------
/**
 * "Each terminal (leaf) node of the game tree has an n-tuple of payoffs,
 *  meaning there is one payoff for each player at the end of every possible play".
 *
 * @param outcomes payoffs
 */
case class ExpectedValue(outcomes : Map[DeliberatePlayer, Double])


object ExpectedValue {
  def apply(values: Seq[Double]):ExpectedValue = {
    val valuesWithIndex: Seq[(Double, Int)] =
      values.zipWithIndex

    val playersWithValues: Seq[(DeliberatePlayer, Double)] =
      valuesWithIndex.map(vi => (DeliberatePlayer(vi._2), vi._1))

    ExpectedValue(
      Map[DeliberatePlayer, Double]()
        ++ playersWithValues)
  }
}