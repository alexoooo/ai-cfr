package ao.learn.mst.gen2.example.incomplete.node

import ao.learn.mst.gen2.game.ExtensiveGameTerminal
import ao.learn.mst.gen2.example.incomplete._
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.player.model.DeliberatePlayer


//----------------------------------------------------------------------------------------------------------------------
case class IncompleteTerminal(
      playerOneType: IncompleteType,
      playerOneAction: IncompleteAction,
      playerTwoAction: IncompleteAction)
    extends ExtensiveGameTerminal
{
  def payoff = {
    val outcomes: (Double, Double) = {
      playerOneType match {
        case IncompleteTypeOne => playerOneAction match {
          case IncompleteActionUp => playerTwoAction match {
            case IncompleteActionUp => (0, 0)
            case IncompleteActionDown => (2, 1)
          }
          case IncompleteActionDown => playerTwoAction match {
              // adjustment: swap 1st player rewards to compensate for non-zero-sum property

//            case IncompleteActionUp => (1, 2)
//            case IncompleteActionDown => (3, 1)
            case IncompleteActionUp => (3, 2)
            case IncompleteActionDown => (1, 1)
          }
        }

        case IncompleteTypeTwo => playerOneAction match {
          case IncompleteActionUp => playerTwoAction match {
            case IncompleteActionUp => (2, 1)
            case IncompleteActionDown => (3, 1)
          }
          case IncompleteActionDown => playerTwoAction match {
            case IncompleteActionUp => (0, 0)
            case IncompleteActionDown => (1, 2)
          }
        }
      }
    }

    ExpectedValue(Map(
      DeliberatePlayer(0) -> outcomes._1,
      DeliberatePlayer(1) -> outcomes._2))
  }
}

