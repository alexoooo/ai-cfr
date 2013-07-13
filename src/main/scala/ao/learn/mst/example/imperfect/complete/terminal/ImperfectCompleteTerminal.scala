package ao.learn.mst.example.imperfect.complete.terminal

import ao.learn.mst.gen2.game.ExtensiveGameTerminal
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.player.model.RationalPlayer


//----------------------------------------------------------------------------------------------------------------------
class ImperfectCompleteTerminal(
      firstPlayerOutcome:Double,
      secondPlayerOutcome:Double)
    extends ExtensiveGameTerminal
{
  def payoff = ExpectedValue(
    Map(
      RationalPlayer(0) -> firstPlayerOutcome,
      RationalPlayer(1) -> secondPlayerOutcome))
}


//----------------------------------------------------------------------------------------------------------------------
object ImperfectCompleteTerminalUpUp
  extends ImperfectCompleteTerminal(0, 0)

object ImperfectCompleteTerminalUpDown
  extends ImperfectCompleteTerminal(2, 1)

object ImperfectCompleteTerminalDownUp
  extends ImperfectCompleteTerminal(1, 2)

object ImperfectCompleteTerminalDownDown
  extends ImperfectCompleteTerminal(3, 1)
