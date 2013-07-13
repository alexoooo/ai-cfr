package ao.learn.mst.example.kuhn.play

import impl.KuhnConsolePlayer
import ao.learn.mst.example.kuhn.card.KuhnDeck
import util.Random


/**
 * Date: 14/11/11
 * Time: 2:03 PM
 */
object KuhnGameRunner
    extends App
{
  //--------------------------------------------------------------------------------------------------------------------
  val firstPlayer = new KuhnConsolePlayer()
  val lastPlayer  = new KuhnConsolePlayer()

  val outcome = new KuhnDealer().playHand(
    firstPlayer,
    lastPlayer,
    new KuhnDeck().deal(new Random()))

  println("outcome is: " + outcome)
}