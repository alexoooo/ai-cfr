package ao.learn.mst.example.kuhn.state

import KuhnPosition._
import terminal.KuhnTerminalStatus._
import ao.learn.mst.example.kuhn.action._
import ao.learn.mst.example.kuhn.action.KuhnActionSequence._
import ao.learn.mst.example.kuhn.view.KuhnObservation
import ao.learn.mst.example.kuhn.action.KuhnActionSequence
import scala.Some
import ao.learn.mst.example.kuhn.action.KuhnCardSequence


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
      case Some(preShutdownWinner) => {
        preShutdownWinner
      }
      case None => {
        cards.winner
      }
    })
  })


  //--------------------------------------------------------------------------------------------------------------------
  def availableActions : Seq[KuhnDecision] =
    terminalStatus match {
      case None => Seq(CheckFold, CallRaise)
      case _    => Seq()
    }


  //--------------------------------------------------------------------------------------------------------------------
  def act(action : KuhnDecision):KuhnState = {
    val (nextActionSequence, nextState) = actions match {
      case Empty => action match {
        case CheckFold =>
          (KuhnActionSequence.Check, stake)

        case CallRaise =>
          (KuhnActionSequence.Raise, stake.incrementFirstPlayer)
      }

      case Check => {
        action match {
          case CheckFold =>
            (KuhnActionSequence.CheckCheck, stake)

          case CallRaise =>
            (KuhnActionSequence.CheckRaise, stake.incrementLastPlayer)
        }
      }

      case CheckRaise => {
        action match {
          case CheckFold =>
            (KuhnActionSequence.CheckRaiseFold, stake)

          case CallRaise =>
            (KuhnActionSequence.CheckRaiseCall, stake.incrementFirstPlayer)
        }
      }

      case Raise => action match {
        case CheckFold =>
          (KuhnActionSequence.RaiseFold, stake)

        case CallRaise =>
          (KuhnActionSequence.RaiseCall, stake.incrementLastPlayer)
      }
    }

    new KuhnState(cards, nextActionSequence, nextState)
  }





  //--------------------------------------------------------------------------------------------------------------------
  def playerView: KuhnObservation =
    KuhnObservation(
      cards.forPlayer( nextToAct.get.id ),
      actions,
      stake)
}
