package ao.learn.mst.gen2.example.kuhn.adapt

import ao.learn.mst.gen2.example.kuhn.state.KuhnState
import ao.learn.mst.gen2.game.{ExtensiveGameNode, ExtensiveGameDecision}
import ao.learn.mst.gen2.player.model.{DeliberatePlayer, IndexedFiniteAction, FiniteAction}
import collection.immutable.SortedSet
import ao.learn.mst.gen2.example.kuhn.action.{KuhnDecision, KuhnPlayerAction}

/**
 * Date: 03/12/11
 * Time: 8:03 PM
 */
case class KuhnGameDecision(delegate : KuhnState)
    extends ExtensiveGameDecision
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions : SortedSet[FiniteAction] = {
    IndexedFiniteAction.sequence(
      delegate.availableActions.length)
  }


  //--------------------------------------------------------------------------------------------------------------------
//  def probabilities : ActionProbabilityMass =
//  {
//    val possibilities = actions
//
//    val uniformProbability : Double =
//      1.0 / possibilities.size
//
//    val possibilityProbabilities = SortedMap[FiniteAction, Double]() ++
//      possibilities.map(_ -> uniformProbability)
//
//    ActionProbabilityMass(
//      possibilityProbabilities)
//  }


  //--------------------------------------------------------------------------------------------------------------------
  def child(action: FiniteAction) : ExtensiveGameNode = {
    val childDelegate = delegate.act(KuhnDecision.values.toSeq(action.index))

    childDelegate.winner match {
      case None => KuhnGameDecision(childDelegate)
      case Some(_) => KuhnGameTerminal(childDelegate)
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  override def player =
    DeliberatePlayer(delegate.nextToAct.get.id)

  def informationSet =
    KuhnGameInfo(
      delegate.cards.forPlayer( player.index ),
      delegate.actions,
      delegate.stake)
}