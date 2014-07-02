package ao.learn.mst.gen5.example.kuhn.state

import KuhnPosition._
import terminal.KuhnTerminalStatus._
import ao.learn.mst.gen5.example.kuhn.action._
import ao.learn.mst.gen5.example.kuhn.action.KuhnActionSequence._
import ao.learn.mst.gen5.example.kuhn.view.KuhnObservation
import ao.learn.mst.gen5.example.kuhn.action.KuhnActionSequence
import scala.Some
import ao.learn.mst.gen5.example.kuhn.action.KuhnCardSequence


//----------------------------------------------------------------------------------------------------------------------
case class KuhnState(
    cards   : KuhnCardSequence,
    actions : KuhnActionSequence,
    stake   : KuhnStake)
{
  //--------------------------------------------------------------------------------------------------------------------
  def this(cards : KuhnCardSequence) =
    this(cards, Empty, new KuhnStake().ante)


  //--------------------------------------------------------------------------------------------------------------------
  def nextToAct : Option[KuhnPosition] = actions match {
      case Empty       => Some(FirstToAct)
      case Check       => Some(LastToAct)
      case CheckCheck  => None
      case CheckRaise  => Some(FirstToAct)
      case Raise       => Some(LastToAct)
      case RaiseFold   => None
      case RaiseCall   => None
    }


  //--------------------------------------------------------------------------------------------------------------------
  def terminalStatus : Option[KuhnTerminalStatus] = actions match {
      case CheckCheck     => Some(SmallShowdown)
      case CheckRaiseFold => Some(LastToActWins)
      case CheckRaiseCall => Some(BigShowdown)
      case RaiseFold      => Some(FirstToActWins)
      case RaiseCall      => Some(BigShowdown)
      case _              => None
    }

  def winner : Option[KuhnPosition] = terminalStatus.flatMap(status => {
    Some(status.preShowdownWinner match {
      case Some(preShutdownWinner) =>
        preShutdownWinner

      case None =>
        cards.winner
    })
  })


  //--------------------------------------------------------------------------------------------------------------------
  def availableActions : Seq[KuhnDecision] =
    terminalStatus match {
      case None => Seq(KuhnCheckFold, KuhnCallRaise)
      case _    => Seq()
    }


  //--------------------------------------------------------------------------------------------------------------------
  def act(action : KuhnDecision):KuhnState = {
    val (nextActionSequence, nextStake) = actions match {
      case Empty => action match {
        case KuhnCheckFold =>
          (KuhnActionSequence.Check, stake)

        case KuhnCallRaise =>
          (KuhnActionSequence.Raise, stake.incrementFirstPlayer)
      }

      case Check =>
        action match {
          case KuhnCheckFold =>
            (KuhnActionSequence.CheckCheck, stake)

          case KuhnCallRaise =>
            (KuhnActionSequence.CheckRaise, stake.incrementLastPlayer)
        }

      case CheckRaise =>
        action match {
          case KuhnCheckFold =>
            (KuhnActionSequence.CheckRaiseFold, stake)

          case KuhnCallRaise =>
            (KuhnActionSequence.CheckRaiseCall, stake.incrementFirstPlayer)
        }

      case Raise => action match {
        case KuhnCheckFold =>
          (KuhnActionSequence.RaiseFold, stake)

        case KuhnCallRaise =>
          (KuhnActionSequence.RaiseCall, stake.incrementLastPlayer)
      }
    }

    new KuhnState(cards, nextActionSequence, nextStake)
  }





  //--------------------------------------------------------------------------------------------------------------------
  def playerView: KuhnObservation =
    KuhnObservation(
      cards.forPlayer( nextToAct.get.id ),
      actions,
      stake)
}
