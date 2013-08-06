package ao.learn.mst.gen3.example

import ao.learn.mst.gen3.game.{NExtensiveGameNode, NExtensiveGameDecision}
import ao.learn.mst.example.kuhn.action.KuhnPlayerAction._
import ao.learn.mst.gen2.player.model.DeliberatePlayer
import scala.Some
import ao.learn.mst.example.kuhn.state.KuhnState
import ao.learn.mst.example.kuhn.action.KuhnDecision

/**
 * 21/07/13 4:54 PM
 */
case class NKuhnGameDecision(delegate : KuhnState)
  extends NExtensiveGameDecision[NKuhnInfoSet, NKuhnAction]
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions: Seq[Decision] =
    delegate.availableActions.map(Decision)

//  private def asLegal[T >: NKuhnAction](action: T): Option[Decision] =
//    action match {
//      case d @ Chance(_) =>
//        if (actions.contains(c)) Some(c) else None
//    }


  //--------------------------------------------------------------------------------------------------------------------
  def player: DeliberatePlayer =
    DeliberatePlayer(delegate.nextToAct.get.id)


  //--------------------------------------------------------------------------------------------------------------------
  /**
   * @return InformationSet from the point of view of "player"
   */
  def informationSet: NKuhnInfoSet =
    NKuhnInfoSet(delegate.playerView)


  //--------------------------------------------------------------------------------------------------------------------
  def child[T >: NKuhnAction](action: T): Option[NExtensiveGameNode[NKuhnInfoSet, NKuhnAction]] = {
    val legalKuhnAction : Option[KuhnDecision] = action match {
      case Decision(kuhnAction) =>
        if (actions.indices.contains(kuhnAction.id)) Some(kuhnAction) else None
    }

    val legalChildDelegate : Option[KuhnState] =
      legalKuhnAction.map(delegate.act)

    legalChildDelegate.map(state =>
      state.winner match {
        case None => NKuhnGameDecision(state)
        case Some(_) => NKuhnGameTerminal(state)
      }
    )
  }

//  {
//    val childDelegate = delegate.act(KuhnAction.values.toSeq(action.index))
//
//    childDelegate.winner match {
//      case None => KuhnGameDecision(childDelegate)
//      case Some(_) => KuhnGameTerminal(childDelegate)
//    }
//  }
}
