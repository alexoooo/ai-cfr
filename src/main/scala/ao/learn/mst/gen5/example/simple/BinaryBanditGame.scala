package ao.learn.mst.gen5.example.simple

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node.{Decision, Terminal, ExtensiveNode}
import ao.learn.mst.gen4.Rational

/**
 *
 */
trait BinaryBanditGame
  extends ExtensiveGame[BinaryBanditState, Unit, Boolean]
{
  def playerCount: Int =
    1

  def initialState: BinaryBanditState =
    BinaryDecisionState


  def node(state: BinaryBanditState): ExtensiveNode[Unit, Boolean] = {
    state match {
      case BinaryTerminalState(action: Boolean) => {
        val outcome : Double =
          payoff(action)

        Terminal(Seq(outcome))
      }

      case BinaryDecisionState =>
        Decision(Rational(0), (), Seq(false, true))
    }
  }


  def transition(
      nonTerminal: BinaryBanditState,
      action: Boolean): BinaryBanditState =
    BinaryTerminalState(action)


  def payoff(choice : Boolean) : Double
}
