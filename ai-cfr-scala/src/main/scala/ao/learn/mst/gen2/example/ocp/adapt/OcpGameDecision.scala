package ao.learn.mst.gen2.example.ocp.adapt

import ao.learn.mst.gen2.game.{ExtensiveGameNode, ExtensiveGameDecision}
import collection.immutable.SortedSet
import ao.learn.mst.gen2.example.ocp.state.OcpState
import ao.learn.mst.gen2.example.ocp.action.OcpAction
import ao.learn.mst.gen2.player.model.{DeliberatePlayer, IndexedFiniteAction, FiniteAction}

/**
 * Date: 03/12/11
 * Time: 8:03 PM
 */
case class OcpGameDecision(delegate: OcpState)
  extends ExtensiveGameDecision
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions: SortedSet[FiniteAction] = {
    IndexedFiniteAction.sequence(
      delegate.availableActions.length)
  }


  //--------------------------------------------------------------------------------------------------------------------
  def child(action: FiniteAction): ExtensiveGameNode = {
    val childDelegate = delegate.act(OcpAction.values.toSeq(action.index))

    childDelegate.winner match {
      case None => OcpGameDecision(childDelegate)
      case Some(_) => OcpGameTerminal(childDelegate)
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  override def player =
    DeliberatePlayer(delegate.nextToAct.get.id)

  def informationSet =
    OcpGameInfo(
      delegate.cards.forPlayer(player.index),
      delegate.actions,
      delegate.stake)
}