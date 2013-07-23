package ao.learn.mst.example.rps

import act.{Scissors, Paper, Rock, RockPaperScissorsAction}
import ao.learn.mst.gen2.game.ExtensiveGameTerminal
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.player.model.DeliberatePlayer


//----------------------------------------------------------------------------------------------------------------------
class RockPaperScissorsTerminal(
      firstPlayerAction : RockPaperScissorsAction,
      secondPlayerAction: RockPaperScissorsAction)
    extends ExtensiveGameTerminal
{
  def payoff = {
    val outcomes: (Double, Double) = {
      firstPlayerAction match {
        case Rock => secondPlayerAction match {
          case Rock => (0, 0)
          case Paper => (-Paper.value, Paper.value)
          case Scissors => (Rock.value, -Rock.value)
        }

        case Paper => secondPlayerAction match {
          case Rock => (Paper.value, -Paper.value)
          case Paper => (0, 0)
          case Scissors => (-Scissors.value, Scissors.value)
        }

        case Scissors => secondPlayerAction match {
          case Rock => (-Rock.value, Rock.value)
          case Paper => (Scissors.value, -Scissors.value)
          case Scissors => (0, 0)
        }
      }
    }

    ExpectedValue(
      Map(DeliberatePlayer(0) -> outcomes._1,
          DeliberatePlayer(1) -> outcomes._2))
  }
}
