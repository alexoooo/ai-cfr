package ao.learn.mst.example.slot.general

import ao.learn.mst.gen2.game.ExtensiveGameTerminal
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.player.model.DeliberatePlayer

//--------------------------------------------------------------------------------------------------------------------
case class SlotMachineTerminal(value: Double) extends ExtensiveGameTerminal {
  def payoff = ExpectedValue(
    Map(DeliberatePlayer(0) -> value))
}
