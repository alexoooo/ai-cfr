package ao.learn.mst.example.imperfect.complete.decision

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen2.game.ExtensiveGameDecision
import ao.learn.mst.example.imperfect.complete.terminal.{ImperfectCompleteTerminalDownDown, ImperfectCompleteTerminalDownUp, ImperfectCompleteTerminalUpDown, ImperfectCompleteTerminalUpUp}
import ao.learn.mst.gen2.player.model.{RationalPlayer, NamedFiniteAction, FiniteAction}


//----------------------------------------------------------------------------------------------------------------------
abstract class ImperfectCompleteDecision
    extends ExtensiveGameDecision
{
  val actions =
    NamedFiniteAction.sequence(
      "U", "D")
}


//----------------------------------------------------------------------------------------------------------------------
case object ImperfectCompleteDecisionFirstInfo extends InformationSet

case object ImperfectCompleteDecisionSecondInfo extends InformationSet


//----------------------------------------------------------------------------------------------------------------------
case object ImperfectCompleteDecisionFirst
  extends ImperfectCompleteDecision
{
  override def player =
    new RationalPlayer(0)

  val informationSet = ImperfectCompleteDecisionFirstInfo

  def child(action: FiniteAction) =
    action.index match {
      case 0 => ImperfectCompleteDecisionAfterUp
      case 1 => ImperfectCompleteDecisionAfterDown
    }
}


//----------------------------------------------------------------------------------------------------------------------
object ImperfectCompleteDecisionAfterUp
    extends ImperfectCompleteDecision
{
  override def player =
    RationalPlayer(1)

  def informationSet = ImperfectCompleteDecisionSecondInfo

  def child(action: FiniteAction) =
    action.index match {
      case 0 => ImperfectCompleteTerminalUpUp
      case 1 => ImperfectCompleteTerminalUpDown
    }
}


//----------------------------------------------------------------------------------------------------------------------
object ImperfectCompleteDecisionAfterDown
    extends ImperfectCompleteDecision
{
  override def player =
    RationalPlayer(1)

  def informationSet = ImperfectCompleteDecisionSecondInfo

  def child(action: FiniteAction) =
    action.index match {
      case 0 => ImperfectCompleteTerminalDownUp
      case 1 => ImperfectCompleteTerminalDownDown
    }
}