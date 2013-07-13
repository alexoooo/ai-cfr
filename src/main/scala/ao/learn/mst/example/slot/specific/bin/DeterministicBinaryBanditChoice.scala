package ao.learn.mst.example.slot.specific.bin

import ao.learn.mst.example.slot.general.{SlotMachineTerminal, SlotMachineDecision}
import ao.learn.mst.gen2.player.model.{NamedFiniteAction, FiniteAction}


case object DeterministicBinaryBanditChoice
    extends SlotMachineDecision
{
  //--------------------------------------------------------------------------------------------------------------------
  val actions = NamedFiniteAction.sequence(
    "U", "D")


  //--------------------------------------------------------------------------------------------------------------------
  def child(action: FiniteAction) = new SlotMachineTerminal(
    action.index match {
      case 0 => 0
      case 1 => 1
    })
}
