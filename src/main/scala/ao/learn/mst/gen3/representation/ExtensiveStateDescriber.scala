package ao.learn.mst.gen3.representation

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen3.NExtensiveAction

/**
 * 09/06/13 10:20 PM
 */
trait ExtensiveStateDescriber[I <: InformationSet, A <: NExtensiveAction]
{
  def describe(informationSet: I): ExtensiveStateView[I, A]
}
