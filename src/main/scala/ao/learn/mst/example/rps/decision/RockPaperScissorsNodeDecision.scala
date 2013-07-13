package ao.learn.mst.example.rps.decision

import ao.learn.mst.gen2.game.{ExtensiveGameNode, ExtensiveGameDecision}
import collection.immutable.SortedSet
import ao.learn.mst.example.rps.act.{RockPaperScissorsAction, Scissors, Paper, Rock}
import ao.learn.mst.example.rps.RockPaperScissorsInfoSet
import ao.learn.mst.gen2.player.model.FiniteAction

abstract class RockPaperScissorsNodeDecision
  extends ExtensiveGameDecision {
  val actions =
    SortedSet[FiniteAction](
      Rock, Paper, Scissors)

  def informationSet =
    RockPaperScissorsInfoSet

  def child(action: FiniteAction) = action match {
    case Rock => child(Rock)
    case Paper => child(Paper)
    case Scissors => child(Scissors)
  }

  def child(action: RockPaperScissorsAction): ExtensiveGameNode
}
