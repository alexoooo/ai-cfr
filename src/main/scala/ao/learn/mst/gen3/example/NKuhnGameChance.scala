package ao.learn.mst.gen3.example

import ao.learn.mst.gen3.game.{NExtensiveGameNode, NExtensiveGameChance}
import ao.learn.mst.example.kuhn.card.{KuhnDeck, KuhnCard}
import ao.learn.mst.example.kuhn.card.KuhnCard._
import ao.learn.mst.example.kuhn.state.KuhnState

/**
 *
 */
case object NKuhnGameChance extends NExtensiveGameChance[NKuhnInfoSet, NKuhnAction]
{
  //--------------------------------------------------------------------------------------------------------------------
  val actions: Set[Chance] =
    KuhnDeck.permutations.map(Chance).toSet

  private def asLegal[T >: NKuhnAction](action: T): Option[Chance] =
    action match {
      case c @ Chance(_) =>
        if (actions.contains(c)) Some(c) else None
    }


  //--------------------------------------------------------------------------------------------------------------------
  def probability[T >: NKuhnAction](action: T): Double =
    asLegal(action)
      .map(_ => 1.0 / actions.size)
      .getOrElse(0.0)


  //--------------------------------------------------------------------------------------------------------------------
  def child[T >: NKuhnAction](action: T): Option[NExtensiveGameNode[NKuhnInfoSet, NKuhnAction]] =
    asLegal(action).map(c =>
      NKuhnGameDecision(
        new KuhnState(c.delegate)))
}
