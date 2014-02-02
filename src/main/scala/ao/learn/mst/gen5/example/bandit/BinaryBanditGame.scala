package ao.learn.mst.gen5.example.bandit

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node.{Rational, Decision, Terminal, ExtensiveNode}

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
