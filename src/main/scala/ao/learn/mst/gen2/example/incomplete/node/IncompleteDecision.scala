package ao.learn.mst.gen2.example.incomplete.node

import ao.learn.mst.gen2.game.ExtensiveGameDecision
import ao.learn.mst.gen2.example.incomplete._
import ao.learn.mst.gen2.info.InformationSet
import collection.immutable.SortedSet
import ao.learn.mst.gen2.player.model.{DeliberatePlayer, FiniteAction}


//----------------------------------------------------------------------------------------------------------------------
abstract class IncompleteDecision extends ExtensiveGameDecision {
  val actions =
    SortedSet[FiniteAction](
      IncompleteActionUp, IncompleteActionDown)
}

abstract class IncompleteDecisionPlayerOne extends IncompleteDecision {
  override def player =
    DeliberatePlayer(0)
}

abstract class IncompleteDecisionPlayerTwo(
    playerOneType : IncompleteType) extends IncompleteDecision {
  override def player =
    DeliberatePlayer(1)
}


//----------------------------------------------------------------------------------------------------------------------
case object IncompleteInfoPlayerOneTypeOne extends InformationSet
case object IncompleteDecisionPlayerOneTypeOne extends IncompleteDecisionPlayerOne
{
  def informationSet =
    IncompleteInfoPlayerOneTypeOne

  def child(action: FiniteAction) =
    action.index match {
      case IncompleteActionUp.index => IncompleteDecisionPlayerTwoAfterUp(IncompleteTypeOne)
      case IncompleteActionDown.index => IncompleteDecisionPlayerTwoAfterDown(IncompleteTypeOne)
    }
}


case object IncompleteInfoPlayerOneTypeTwo extends InformationSet
case object IncompleteDecisionPlayerOneTypeTwo extends IncompleteDecisionPlayerOne
{
  def informationSet =
    IncompleteInfoPlayerOneTypeTwo

  def child(action: FiniteAction) =
    action.index match {
      case IncompleteActionUp.index => IncompleteDecisionPlayerTwoAfterUp(IncompleteTypeTwo)
      case IncompleteActionDown.index => IncompleteDecisionPlayerTwoAfterDown(IncompleteTypeTwo)
    }
}



//----------------------------------------------------------------------------------------------------------------------
case object IncompleteInfoPlayerTwoAfterUp extends InformationSet
case class IncompleteDecisionPlayerTwoAfterUp(playerOneType : IncompleteType)
  extends IncompleteDecisionPlayerTwo(playerOneType)
{
  def informationSet =
    IncompleteInfoPlayerTwoAfterUp

  def child(action: FiniteAction) =
    action.index match {
      case IncompleteActionUp.index   => IncompleteTerminal(playerOneType, IncompleteActionUp, IncompleteActionUp)
      case IncompleteActionDown.index => IncompleteTerminal(playerOneType, IncompleteActionUp, IncompleteActionDown)
    }
}


case object IncompleteInfoPlayerTwoAfterDown extends InformationSet
case class IncompleteDecisionPlayerTwoAfterDown(playerOneType : IncompleteType)
  extends IncompleteDecisionPlayerTwo(playerOneType)
{
  def informationSet = IncompleteInfoPlayerTwoAfterDown

  def child(action: FiniteAction) =
    action.index match {
      case IncompleteActionUp.index   => IncompleteTerminal(playerOneType, IncompleteActionDown, IncompleteActionUp)
      case IncompleteActionDown.index => IncompleteTerminal(playerOneType, IncompleteActionDown, IncompleteActionDown)
    }
}


