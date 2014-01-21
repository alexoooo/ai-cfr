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
 * +-- True
 *     |
 *     +-- False = 3
 *     |
 *     +-- True = 4
 * 
 */
case object DeterministicBinaryForkGame
  extends ExtensiveGame[
    DeterministicBinaryForkState,
    DeterministicBinaryForkState,
    Boolean]
{
  def playerCount: Int =
    1


  def initialState: DeterministicBinaryForkState =
    DeterministicBinaryForkState(None, None)


  def node(state: DeterministicBinaryForkState): ExtensiveNode[DeterministicBinaryForkState, Boolean] =
    state match {
      case DeterministicBinaryForkState(Some(false), Some(false)) =>
        Terminal(Seq(1))

      case DeterministicBinaryForkState(Some(false), Some(true)) =>
        Terminal(Seq(2))

      case DeterministicBinaryForkState(Some(true), Some(false)) =>
        Terminal(Seq(3))

      case DeterministicBinaryForkState(Some(true), Some(true)) =>
        Terminal(Seq(4))

      case DeterministicBinaryForkState(Some(false), None) =>
        Decision(0, state, Seq(false, true))

      case DeterministicBinaryForkState(Some(true), None) =>
        Decision(0, state, Seq(false, true))

      case DeterministicBinaryForkState(None, None) =>
        Decision(0, state, Seq(false, true))
    }


  def transition(nonTerminal: DeterministicBinaryForkState, action: Boolean): DeterministicBinaryForkState =
    nonTerminal match {
      case DeterministicBinaryForkState(None, None) =>
        DeterministicBinaryForkState(Some(action), None)

      case DeterministicBinaryForkState(Some(first), None) =>
        DeterministicBinaryForkState(Some(first), Some(action))
    }
}


case class DeterministicBinaryForkState(
  firstAction  : Option[Boolean],
  secondAction : Option[Boolean])