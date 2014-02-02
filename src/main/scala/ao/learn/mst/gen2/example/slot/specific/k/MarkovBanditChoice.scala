package ao.learn.mst.gen2.example.slot.specific.k

import ao.learn.mst.gen2.example.slot.general.{SlotMachineTerminal, SlotMachineDecision}
import ao.learn.mst.gen2.player.model.{NamedFiniteAction, FiniteAction}


case class MarkovBanditChoice(k: Int)
    extends SlotMachineDecision
{
  //--------------------------------------------------------------------------------------------------------------------
  val actions = NamedFiniteAction.sequence(
    (1 to k).map(_.toString) : _*)


  //--------------------------------------------------------------------------------------------------------------------
  def child(action: FiniteAction) = new SlotMachineTerminal(
    (1 + action.index) * scala.math.random)
}


