package ao.learn.mst.example.slot.specific.k

import ao.learn.mst.example.slot.general.SlotMachineGame

/**
 * Stochastic k-armed bandit.
 *
 * Perfect play:
 * always last (highest index) action.
 */
case class MarkovBanditGame(k: Int)
    extends SlotMachineGame
{
  val treeRoot =
    new MarkovBanditChoice(k)
}