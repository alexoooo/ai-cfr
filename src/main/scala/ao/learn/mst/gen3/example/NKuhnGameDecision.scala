package ao.learn.mst.gen3.example

import ao.learn.mst.example.kuhn.state.KuhnState
import ao.learn.mst.gen3.game.{NExtensiveGameNode, NExtensiveGameDecision}
import ao.learn.mst.gen2.player.model.RationalPlayer

/**
 * 21/07/13 4:54 PM
 */
case class NKuhnGameDecision(delegate : KuhnState)
  extends NExtensiveGameDecision[NKuhnInfoSet, NKuhnAction]
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions: Traversable[NKuhnAction] = ???


  //--------------------------------------------------------------------------------------------------------------------
  def child[T >: NKuhnAction](action: T): Option[NExtensiveGameNode[NKuhnInfoSet, NKuhnAction]] = ???

  /**
   * @return RationalPlayer, i.e. next to act.
   */
  override def player: RationalPlayer = ???

  /**
   * @return InformationSet from the point of view of "player"
   */
  def informationSet: NKuhnInfoSet = ???
}
