package ao.learn.mst.gen3.abstraction

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen3.NExtensiveAction

/**
 * 09/06/13 10:12 PM
 */
trait ExtensiveGameAbstraction[I <: InformationSet, A <: NExtensiveAction]
{
  //--------------------------------------------------------------------------------------------------------------------
  def infoSetCount: Int

  def infoSetIndex(informationSet: I): Int

  def actionCount(informationSet: I): Int

  def actionIndex(informationSet: I, action: A): Int
}
