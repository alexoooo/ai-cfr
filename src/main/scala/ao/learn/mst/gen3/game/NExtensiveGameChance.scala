package ao.learn.mst.gen3.game

import ao.learn.mst.gen2.player.model.ChancePlayer
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen3.NExtensiveAction

/**
 * 09/06/13 10:06 PM
 */
trait NExtensiveGameChance[I <: InformationSet, A <: NExtensiveAction]
    extends NExtensiveGameNonTerminal[I, A]
{
  //--------------------------------------------------------------------------------------------------------------------
  override def player = ChancePlayer


  //--------------------------------------------------------------------------------------------------------------------
  def actionProbabilities : Map[A, Double]
}