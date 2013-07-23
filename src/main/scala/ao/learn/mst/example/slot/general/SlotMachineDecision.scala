package ao.learn.mst.example.slot.general

import ao.learn.mst.gen2.game.ExtensiveGameDecision
import ao.learn.mst.gen2.player.model.DeliberatePlayer


abstract class SlotMachineDecision extends ExtensiveGameDecision
{
  override def player = DeliberatePlayer(0)

  def informationSet = SlotMachineInfoSet
}