package ao.learn.mst.example.kuhn.state

import KuhnPosition._
import terminal.KuhnTerminalStatus._
import ao.learn.mst.example.kuhn.action.KuhnAction
import ao.learn.mst.example.kuhn.action.KuhnAction._
import ao.learn.mst.example.kuhn.action.KuhnActionSequence
import ao.learn.mst.example.kuhn.action.KuhnActionSequence._
import ao.learn.mst.example.kuhn.card.KuhnCardSequence


//----------------------------------------------------------------------------------------------------------------------
case class KuhnState(
    cards   : KuhnCardSequence,
    actions : KuhnActionSequence,
    stake   : KuhnStake)
{
  //--------------------------------------------------------------------------------------------------------------------
  def this(cards : KuhnCardSequence) =
    this(cards, FirstAction, new KuhnStake().ante)


  //--------------------------------------------------------------------------------------------------------------------
  def nextToAct : Option[KuhnPosition] = actions match {
      case FirstAction => Some(FirstToAct)
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
  def availableActions : Seq[KuhnAction] =
    terminalStatus match {
      case None => Seq(KuhnAction.CheckFold, KuhnAction.CallRaise)
      case _    => Seq()
    }


  //--------------------------------------------------------------------------------------------------------------------
  def act(action : KuhnAction):KuhnState =
    actions match {
      case FirstAction => action match {
        case CheckFold =>
          new KuhnState(cards, KuhnActionSequence.Check, stake)

        case CallRaise =>
          new KuhnState(cards, KuhnActionSequence.Raise, stake.incrementFirstPlayer)
      }

      case Check => {
        action match {
          case CheckFold =>
            new KuhnState(cards, KuhnActionSequence.CheckCheck, stake)

          case CallRaise =>
            new KuhnState(cards, KuhnActionSequence.CheckRaise, stake.incrementLastPlayer)
        }
      }

      case CheckRaise => {
        action match {
          case CheckFold =>
            new KuhnState(cards, KuhnActionSequence.CheckRaiseFold, stake)

          case CallRaise =>
            new KuhnState(cards, KuhnActionSequence.CheckRaiseCall, stake.incrementFirstPlayer)
        }
      }

      case Raise => action match {
        case CheckFold =>
          new KuhnState(cards, KuhnActionSequence.RaiseFold, stake)

        case CallRaise =>
          new KuhnState(cards, KuhnActionSequence.RaiseCall, stake.incrementLastPlayer)
      }
    }
}
