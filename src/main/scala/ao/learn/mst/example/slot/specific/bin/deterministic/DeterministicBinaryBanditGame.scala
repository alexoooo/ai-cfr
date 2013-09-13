package ao.learn.mst.example.slot.specific.bin.deterministic

import ao.learn.mst.example.slot.general.SlotMachineGame

/**
 * Simplest game possible:
 *   deterministic binary one-armed bandit.
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
