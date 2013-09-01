package ao.learn.mst.gen4.example

import ao.learn.mst.gen4.ExtensiveGame


/**
 * 04/08/13 7:29 PM
 */
object Tournament extends App
{
  val game : ExtensiveGame[_, _, _] =
    KuhnGame

  val t : Tournament[_, _, _] =
    new Tournament(game)

  t.perform()
}


class Tournament[S, A, I]
  (game : ExtensiveGame[S, A, I])
{
  def perform() {
    println(s"game: $game")

    val root = game.initialState
    println(s"root: $root")

    val rootType = game.identify(root)
    println(s"rootType: $rootType")
  }
}
