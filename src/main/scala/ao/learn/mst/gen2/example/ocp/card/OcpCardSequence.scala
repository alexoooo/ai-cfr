package ao.learn.mst.gen2.example.ocp.card

import OcpCard._
import ao.learn.mst.gen2.example.ocp.state.OcpPosition._


//---------------------------------------------------------------------------------------------------------------------
case class OcpCardSequence(
    first: OcpCard, last: OcpCard)
{
  //--------------------------------------------------------------------------
  def hands = (first, last)


  //--------------------------------------------------------------------------
  def winner =
    if (first > last) FirstToAct else LastToAct


  //--------------------------------------------------------------------------
  def forPlayer(index: Int): OcpCard = {
    require((0 until hands.productArity) contains index)

    index match {
      case 0 => first
      case 1 => last
    }
  }
}