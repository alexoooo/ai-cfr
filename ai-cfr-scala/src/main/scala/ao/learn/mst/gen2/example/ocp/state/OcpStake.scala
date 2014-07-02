package ao.learn.mst.gen2.example.ocp.state

import OcpPosition._


//----------------------------------------------------------------------------------------------------------------------
case class OcpStake(
    firstPlayer: Int,
    lastPlayer: Int)
{
  def this() =
    this(0, 0)


  //--------------------------------------------------------------------------------------------------------------------
  def ante: OcpStake =
    OcpStake(firstPlayer + 1, lastPlayer + 1)

  def incrementFirstPlayer: OcpStake =
    OcpStake(firstPlayer + 1, lastPlayer)

  def incrementLastPlayer: OcpStake =
    OcpStake(firstPlayer, lastPlayer + 1)


  //--------------------------------------------------------------------------------------------------------------------
  def total = firstPlayer + lastPlayer


  //--------------------------------------------------------------------------------------------------------------------
  def toOutcome(winner: OcpPosition): OcpOutcome = {
    winner match {
      case OcpPosition.FirstToAct =>
        OcpOutcome(lastPlayer, -lastPlayer)

      case OcpPosition.LastToAct =>
        OcpOutcome(-firstPlayer, firstPlayer)
    }
  }
}