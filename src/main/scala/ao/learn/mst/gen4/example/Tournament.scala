package ao.learn.mst.gen4.example

import ao.learn.mst.gen4.ExtensiveGame


/**
 * 04/08/13 7:29 PM
 */
object Tournament extends App
{
  val game : ExtensiveGame[_, _, _] =
    KuhnGame

  println(s"game: $game")
}
