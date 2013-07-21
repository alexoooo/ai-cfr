package ao.learn.mst.gen3.example

import ao.learn.mst.gen3.game.{NExtensiveGameChance, NExtensiveGame}
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen3.NExtensiveAction
import ao.learn.mst.example.kuhn.card.{KuhnCardSequence, KuhnCard}

/**
 * 13/07/13 8:27 PM
 */
object NExtensiveGameTournament extends App
{
  val game : NExtensiveGame[InformationSet, NExtensiveAction] =
    NKuhnGame

  println(s"Number of rational players: ${game.rationalPlayerCount}")

  val root = game.treeRoot
  println(s"Initial state: $root")
  println(s"First actions: ${root.actions}")

  root match {
    case c : NExtensiveGameChance[_, _] => {
      val illegalAction = Chance(KuhnCardSequence(KuhnCard.King, KuhnCard.King))
      println(s"Probability of illegal: ${c.probability(illegalAction)}")

      val legalAction = Chance(KuhnCardSequence(KuhnCard.King, KuhnCard.Jack))
      println(s"Probability of legal: ${c.probability(legalAction)}")

      val nextState = c.child(legalAction)
      println(s"Next state: $nextState")
    }
  }
}
