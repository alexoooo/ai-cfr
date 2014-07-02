package ao.learn.mst.gen2.example.zerosum.node

import ao.learn.mst.gen2.game.ExtensiveGameDecision
import ao.learn.mst.gen2.example.zerosum.act._
import ao.learn.mst.gen2.example.zerosum.info.ZeroSumInfoBlue
import collection.immutable.SortedSet
import ao.learn.mst.gen2.player.model.{DeliberatePlayer, FiniteAction}


case class ZeroSumDecisionBlue(redPlayerAction: ZeroSumActionRed)
  extends ExtensiveGameDecision
{
  val actions =
    SortedSet[FiniteAction](
      ZeroSumActionBlueA,
      ZeroSumActionBlueB,
      ZeroSumActionBlueC)

  def child(action: FiniteAction) = action.index match {
    case ZeroSumActionBlueA.index => ZeroSumTerminal(redPlayerAction, ZeroSumActionBlueA)
    case ZeroSumActionBlueB.index => ZeroSumTerminal(redPlayerAction, ZeroSumActionBlueB)
    case ZeroSumActionBlueC.index => ZeroSumTerminal(redPlayerAction, ZeroSumActionBlueC)
  }

  override val player =
    DeliberatePlayer(1)

  val informationSet = ZeroSumInfoBlue
}

