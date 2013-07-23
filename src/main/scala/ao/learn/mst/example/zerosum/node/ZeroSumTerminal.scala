package ao.learn.mst.example.zerosum.node

import ao.learn.mst.gen2.game.ExtensiveGameTerminal
import ao.learn.mst.example.zerosum.act._
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.player.model.DeliberatePlayer


case class ZeroSumTerminal (
    redPlayerAction: ZeroSumActionRed,
    bluePlayerAction: ZeroSumActionBlue)
  extends ExtensiveGameTerminal
{
  def payoff = {
    val outcomes: (Double, Double) = {
      redPlayerAction match {
        case ZeroSumActionRedOne => bluePlayerAction match {
          case ZeroSumActionBlueA => (30, -30)
          case ZeroSumActionBlueB => (-10, 10)
          case ZeroSumActionBlueC => (20, -20)
        }
        case ZeroSumActionRedTwo => bluePlayerAction match {
          case ZeroSumActionBlueA => (10, -10)
          case ZeroSumActionBlueB => (20, -20)
          case ZeroSumActionBlueC => (-20, 20)
        }
      }
    }

    ExpectedValue(Map(
      DeliberatePlayer(0) -> outcomes._1,
      DeliberatePlayer(1) -> outcomes._2))
  }
}


