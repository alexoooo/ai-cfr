package ao.learn.mst.example.slot.general

import ao.learn.mst.gen2.game.ExtensiveGameDecision
import ao.learn.mst.gen2.player.model.RationalPlayer


abstract class SlotMachineDecision extends ExtensiveGameDecision
{
  override def player = RationalPlayer(0)

  def informationSet = SlotMachineInfoSet
}