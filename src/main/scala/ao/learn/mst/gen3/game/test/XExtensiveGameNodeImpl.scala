package ao.learn.mst.gen3.game.test

import ao.learn.mst.example.kuhn.adapt.KuhnGameInfo
import ao.learn.mst.gen3.example.{Chance, NKuhnAction, NKuhnInfoSet}
import ao.learn.mst.gen3.game.NExtensiveGameNode

/**
 * 21/07/13 2:53 PM
 */
class XExtensiveGameNodeImpl extends XExtensiveGameNode[NKuhnInfoSet, NKuhnAction]
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions: Traversable[NKuhnAction] = {
    Set[Chance]()
  }


  //--------------------------------------------------------------------------------------------------------------------
  def child[T >: NKuhnAction](action: T): NExtensiveGameNode[NKuhnInfoSet, NKuhnAction] = ???
}
