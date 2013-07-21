package ao.learn.mst.gen3.game.test

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen3.NExtensiveAction
import ao.learn.mst.gen3.game.NExtensiveGameNode

/**
 * 21/07/13 2:27 PM
 */
trait XExtensiveGameNode[+I <: InformationSet, A <: NExtensiveAction]
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions: Traversable[A]


  //--------------------------------------------------------------------------------------------------------------------
  def child[T >: A](action : T) : NExtensiveGameNode[I, A]
}
