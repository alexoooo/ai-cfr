package ao.learn.mst.example.zerosum.act

import ao.learn.mst.gen2.player.model.FiniteAction

//----------------------------------------------------------------------------------------------------------------------
sealed abstract class ZeroSumActionBlue(index : Int)
  extends FiniteAction(index)

case object ZeroSumActionBlueA extends ZeroSumActionBlue(0)
case object ZeroSumActionBlueB extends ZeroSumActionBlue(1)
case object ZeroSumActionBlueC extends ZeroSumActionBlue(2)