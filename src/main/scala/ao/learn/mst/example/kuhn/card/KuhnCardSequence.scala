package ao.learn.mst.example.kuhn.card

import KuhnCard._
import ao.learn.mst.example.kuhn.state.KuhnPosition._


//---------------------------------------------------------------------------------------------------------------------
case class KuhnCardSequence(
    first : KuhnCard, last : KuhnCard)
{
  //--------------------------------------------------------------------------
  def hands = (first, last)


  //--------------------------------------------------------------------------
  def winner =
    if (first > last) FirstToAct else LastToAct


  //--------------------------------------------------------------------------
  def forPlayer(index: Int): KuhnCard = {
    require((0 until hands.productArity) contains index)

    index match {
      case 0 => first
      case 1 => last
    }
  }
}