package ao.learn.mst.gen5.example.bandit.deterministic

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node.{Decision, Terminal, ExtensiveNode}


/**
 * Game Tree:
 *
 * Root
 * |
 * +-- False
 * |   |
 * |   +-- False = 1
 * |   |
 * |   +-- True = 2
 * |
 * +-- True = 3
 *
 */
case object DeterministicBinaryBranchGame
  extends ExtensiveGame[
    DeterministicBinaryBranchState,
    DeterministicBinaryBranchState,
    Boolean]
{
  def playerCount: Int =
    1


  def initialState: DeterministicBinaryBranchState =
    DeterministicBinaryBranchState(None, None)


  def node(
    state: DeterministicBinaryBranchState)
    : ExtensiveNode[DeterministicBinaryBranchState, Boolean] =
  {
    state match {
      case DeterministicBinaryBranchState(Some(false), Some(false)) =>
        Terminal(Seq(1))

      case DeterministicBinaryBranchState(Some(false), Some(true)) =>
        Terminal(Seq(2))

      case DeterministicBinaryBranchState(Some(true), None) =>
        Terminal(Seq(3))

      case DeterministicBinaryBranchState(Some(false), None) =>
        Decision(0, state, Seq(false, true))

      case DeterministicBinaryBranchState(None, None) =>
        Decision(0, state, Seq(false, true))
    }
  }


  def transition(
    nonTerminal: DeterministicBinaryBranchState,
    action: Boolean)
    : DeterministicBinaryBranchState =
  {
    nonTerminal match {
      case DeterministicBinaryBranchState(None, None) =>
        DeterministicBinaryBranchState(Some(action), None)

      case DeterministicBinaryBranchState(Some(first), None) =>
        DeterministicBinaryBranchState(Some(first), Some(action))
    }
  }
}

case class DeterministicBinaryBranchState(
  firstAction  : Option[Boolean],
  secondAction : Option[Boolean])

