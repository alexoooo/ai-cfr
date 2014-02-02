package ao.learn.mst.gen2.example.slot.specific.bin.deterministic

import ao.learn.mst.gen2.example.slot.general.{SlotMachineTerminal, SlotMachineDecision}
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
