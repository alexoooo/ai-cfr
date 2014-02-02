package ao.learn.mst.gen2.example.ocp.state


import ao.learn.mst.gen2.example.ocp.action.OcpAction
import ao.learn.mst.gen2.example.ocp.action.OcpAction._
import ao.learn.mst.gen2.example.ocp.action.OcpActionSequence
import ao.learn.mst.gen2.example.ocp.action.OcpActionSequence._
import ao.learn.mst.gen2.example.ocp.card.OcpCardSequence
import OcpTerminalStatus._
import OcpPosition._


//----------------------------------------------------------------------------------------------------------------------
case class OcpState(
    cards: OcpCardSequence,
    actions: OcpActionSequence,
    stake: OcpStake)
{
  //--------------------------------------------------------------------------------------------------------------------
  def this(cards: OcpCardSequence) =
    this(cards, FirstAction, new OcpStake().ante)


  //--------------------------------------------------------------------------------------------------------------------
  def nextToAct: Option[OcpPosition] = actions match {
    case FirstAction => Some(FirstToAct)
    case Check => Some(LastToAct)
    case CheckCheck => None
    case CheckRaise => Some(FirstToAct)
    case Raise => Some(LastToAct)
    case RaiseFold => None
    case RaiseCall => None
  }


  //--------------------------------------------------------------------------------------------------------------------
  def terminalStatus: Option[OcpTerminalStatus] = actions match {
    case CheckCheck => Some(SmallShowdown)
    case CheckRaiseFold => Some(LastToActWins)
    case CheckRaiseCall => Some(BigShowdown)
    case RaiseFold => Some(FirstToActWins)
    case RaiseCall => Some(BigShowdown)
    case _ => None
  }

  def winner: Option[OcpPosition] = terminalStatus.flatMap(status => {
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
  def availableActions: Seq[OcpAction] =
    terminalStatus match {
      case None => Seq(OcpAction.CheckFold, OcpAction.CallRaise)
      case _ => Seq()
    }


  //--------------------------------------------------------------------------------------------------------------------
  def act(action: OcpAction): OcpState =
    actions match {
      case FirstAction => action match {
        case CheckFold =>
          new OcpState(cards, OcpActionSequence.Check, stake)

        case CallRaise =>
          new OcpState(cards, OcpActionSequence.Raise, stake.incrementFirstPlayer)
      }

      case Check => {
        action match {
          case CheckFold =>
            new OcpState(cards, OcpActionSequence.CheckCheck, stake)

          case CallRaise =>
            new OcpState(cards, OcpActionSequence.CheckRaise, stake.incrementLastPlayer)
        }
      }

      case CheckRaise => {
        action match {
          case CheckFold =>
            new OcpState(cards, OcpActionSequence.CheckRaiseFold, stake)

          case CallRaise =>
            new OcpState(cards, OcpActionSequence.CheckRaiseCall, stake.incrementFirstPlayer)
        }
      }

      case Raise => action match {
        case CheckFold =>
          new OcpState(cards, OcpActionSequence.RaiseFold, stake)

        case CallRaise =>
          new OcpState(cards, OcpActionSequence.RaiseCall, stake.incrementLastPlayer)
      }
    }
}
