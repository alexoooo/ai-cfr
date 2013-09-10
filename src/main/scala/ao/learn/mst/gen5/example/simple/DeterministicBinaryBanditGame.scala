package ao.learn.mst.gen5.example.simple

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node.{Terminal, Decision, ExtensiveNode}
import ao.learn.mst.gen4.Rational


//----------------------------------------------------------------------------------------------------------------------
case object DeterministicBinaryBanditGame
  extends ExtensiveGame[DeterministicBinaryBanditState, Unit, Boolean]
{
  //--------------------------------------------------------------------------------------------------------------------
  def playerCount: Int =
    1

  def initialState =
    DecisionState

  def node(state: DeterministicBinaryBanditState): ExtensiveNode[Unit, Boolean] = {
    state match {
      case TerminalState(action: Boolean) => {
        val payoff : Double =
          math.random * (if (action) 100.0 else 99.0)

        Terminal(Seq(payoff))
      }

      case DecisionState =>
        Decision(Rational(0), (), Seq(false, true))
    }
  }

  def transition(nonTerminal: DeterministicBinaryBanditState, action: Boolean) =
    TerminalState(action)
}
