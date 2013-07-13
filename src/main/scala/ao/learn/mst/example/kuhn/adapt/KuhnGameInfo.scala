package ao.learn.mst.example.kuhn.adapt

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.example.kuhn.card.KuhnCard._
import ao.learn.mst.example.kuhn.action.KuhnActionSequence._
import ao.learn.mst.example.kuhn.state.KuhnStake


case class KuhnGameInfo(
    playerCard: KuhnCard,
    actionSequence: KuhnActionSequence,
    stake: KuhnStake) extends InformationSet
{
  def contains(playerCard: KuhnCard, actionSequence: KuhnActionSequence): Boolean =
    this.playerCard == playerCard && this.actionSequence == actionSequence
}
