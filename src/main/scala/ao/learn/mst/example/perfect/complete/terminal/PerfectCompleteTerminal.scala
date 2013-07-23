package ao.learn.mst.example.perfect.complete.terminal

import scala.Double
import ao.learn.mst.gen2.game.ExtensiveGameTerminal
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.player.model.DeliberatePlayer


//----------------------------------------------------------------------------------------------------------------------
class PerfectCompleteTerminal(
      firstPlayerOutcome: Double,
      secondPlayerOutcome: Double)
    extends ExtensiveGameTerminal
{
  val payoff = ExpectedValue(
    Map(
      DeliberatePlayer(0) -> firstPlayerOutcome,
      DeliberatePlayer(1) -> secondPlayerOutcome))
}


//----------------------------------------------------------------------------------------------------------------------
case object PerfectCompleteTerminalUpUp
  extends PerfectCompleteTerminal(0, 0)

case object PerfectCompleteTerminalUpDown
  extends PerfectCompleteTerminal(2, 1)

case object PerfectCompleteTerminalDownUp
  extends PerfectCompleteTerminal(1, 2)

case object PerfectCompleteTerminalDownDown
  extends PerfectCompleteTerminal(3, 1)

