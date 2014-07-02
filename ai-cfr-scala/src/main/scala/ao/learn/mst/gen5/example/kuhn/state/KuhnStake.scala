package ao.learn.mst.gen5.example.kuhn.state

import KuhnPosition._


//----------------------------------------------------------------------------------------------------------------------
case class KuhnStake(
    firstPlayer : Int,
    lastPlayer  : Int)
{
  def this() =
    this(0, 0)


  //--------------------------------------------------------------------------------------------------------------------
  def ante : KuhnStake =
    KuhnStake(firstPlayer + 1, lastPlayer + 1)

  def incrementFirstPlayer : KuhnStake =
    KuhnStake(firstPlayer + 1, lastPlayer)

  def incrementLastPlayer : KuhnStake =
    KuhnStake(firstPlayer, lastPlayer + 1)


  //--------------------------------------------------------------------------------------------------------------------
  def total = firstPlayer + lastPlayer


  //--------------------------------------------------------------------------------------------------------------------
  def toOutcome(winner : KuhnPosition) : KuhnOutcome = {
    winner match {
      case KuhnPosition.FirstToAct =>
        KuhnOutcome( lastPlayer, -lastPlayer)

      case KuhnPosition.LastToAct =>
        KuhnOutcome(-firstPlayer, firstPlayer)
    }
  }
}