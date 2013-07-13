package ao.learn.mst.example.ocp.adapt

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.example.ocp.state.OcpStake
import ao.learn.mst.example.ocp.card.OcpCard._
import ao.learn.mst.example.ocp.action.OcpActionSequence._

case class OcpGameInfo(
    playerCard: OcpCard,
    actionSequence: OcpActionSequence,
    stake: OcpStake) extends InformationSet
{
  def contains(playerCard: OcpCard, actionSequence: OcpActionSequence): Boolean =
    this.playerCard == playerCard && this.actionSequence == actionSequence
}
