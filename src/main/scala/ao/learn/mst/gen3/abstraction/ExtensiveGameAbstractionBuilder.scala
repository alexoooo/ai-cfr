package ao.learn.mst.gen3.abstraction

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen3.game.NExtensiveGame
import ao.learn.mst.gen3.representation.ExtensiveStateDescriber
import ao.learn.mst.gen3.NExtensiveAction

/**
 * 09/06/13 10:21 PM
 */
trait ExtensiveGameAbstractionBuilder
{
  def buildExtensiveGameAbstraction
    [I <: InformationSet, A <: NExtensiveAction] (
      extensiveGame: NExtensiveGame[I, A],
      informationSetDescriber: ExtensiveStateDescriber[I, A]
    ): ExtensiveGameAbstraction[I, A]
}
