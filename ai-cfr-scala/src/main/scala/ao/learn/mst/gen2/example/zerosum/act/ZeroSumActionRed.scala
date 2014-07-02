package ao.learn.mst.gen2.example.zerosum.act

import ao.learn.mst.gen2.player.model.FiniteAction

//----------------------------------------------------------------------------------------------------------------------
sealed abstract class ZeroSumActionRed(index : Int)
  extends FiniteAction(index)

case object ZeroSumActionRedOne extends ZeroSumActionRed(0)
case object ZeroSumActionRedTwo extends ZeroSumActionRed(1)