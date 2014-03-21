package ao.learn.mst.gen5.example.perfect.complete

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node.{Terminal, Decision, ExtensiveNode}

/**
 * Fully deterministic, 2 players, each gets 1 binary choice.
 *
 * Game tree for at:
 *  http://en.wikipedia.org/wiki/Extensive-form_game#Perfect_and_complete_information
 *
 * Perfect play (where U = first action (index 0), and D = second action (index 1)):
 *  Player 1 will play U and then
 *    player 2 plays D.
 *  If Player 1 plays D (sub-optimal), then
 *    player 2 plays U.
 *
 * In this implementation, false => "Down" and true => "Up".
 */
case object PerfectCompleteGame
  extends
    ExtensiveGame[
      PerfectCompleteState,
      PerfectCompleteState,
      PerfectCompleteAction]
{
  def playerCount: Int =
    2


  def initialState: PerfectCompleteState =
    PerfectCompleteState(
      Seq.empty)


  def node(
      state: PerfectCompleteState)
    : ExtensiveNode[PerfectCompleteState, PerfectCompleteAction] =
  {
    val actionValues : Seq[Boolean] =
      Seq(false, true)

    state.actionSequence.toList match {
      case Nil =>
        Decision(0, state, actionValues.map(PerfectPlayerOneAction))

      case List(actionOne : PerfectPlayerOneAction) =>
        Decision(1, state, actionValues.map(PerfectPlayerTwoAction))

      case List(
          actionOne : PerfectPlayerOneAction,
          actionTwo : PerfectPlayerTwoAction) =>

        val outcome : (Double, Double) =
          (actionOne.decision, actionTwo.decision) match {
            case (true , true ) => (0, 0)
            case (true , false) => (2, 1)
            case (false, true ) => (1, 2)
            case (false, false) => (3, 1)
          }

        Terminal(
          Seq(outcome._1, outcome._2))
    }
  }


  def transition(nonTerminal: PerfectCompleteState, action: PerfectCompleteAction): PerfectCompleteState =
    PerfectCompleteState(
      nonTerminal.actionSequence :+ action)
}
