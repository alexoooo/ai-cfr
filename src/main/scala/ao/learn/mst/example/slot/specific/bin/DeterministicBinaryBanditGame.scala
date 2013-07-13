package ao.learn.mst.example.slot.specific.bin

import ao.learn.mst.example.slot.general.SlotMachineGame

/**
 * Simplest game possible:
 *   deterministic one armed bandit.
 *
 * Perfect play:
 * always "D" (2nd move, indexed 1).
 */
case object DeterministicBinaryBanditGame
  extends SlotMachineGame
{
  //--------------------------------------------------------------------------------------------------------------------
  def treeRoot =
    DeterministicBinaryBanditChoice
}
