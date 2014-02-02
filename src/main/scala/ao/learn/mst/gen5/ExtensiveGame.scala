package ao.learn.mst.gen5

import ao.learn.mst.gen5.node._
import ao.learn.mst.gen5.node.StateDecision


//----------------------------------------------------------------------------------------------------------------------
/**
 * Extensive form game: http://en.wikipedia.org/wiki/Extensive-form_game.
 * Designed for ease of use of AI writing.
 *
 *
 * @tparam State extensive game state
 * @tparam InformationSet what can be seen during decision points
 * @tparam Action possible actions (both deliberate and chance)
 */
trait ExtensiveGame[State, InformationSet, Action]
{
  //--------------------------------------------------------------------------------------------------------------------
  /**
   * @return number of deliberate (aka rational, non-chance) players in the game
   */
  def playerCount : Int


  //--------------------------------------------------------------------------------------------------------------------
  /**
   * @return root of the game state tree
   */
  def initialState : State

  def initialStateNode : ExtensiveStateNode[State, InformationSet, Action] =
    stateNode(initialState)



  //--------------------------------------------------------------------------------------------------------------------
  /**
   * @param state of node of the game tree
   * @return game tree node
   */
  def node(state : State) : ExtensiveNode[InformationSet, Action]

  def stateNode(state : State) : ExtensiveStateNode[State, InformationSet, Action] =
    node(state) match {
      case decision: Decision[InformationSet, Action] =>
        StateDecision(state, decision)

      case chance: Chance[InformationSet, Action] =>
        StateChance(state, chance)

      case terminal: Terminal[InformationSet, Action] =>
        StateTerminal(state, terminal)
    }


  //--------------------------------------------------------------------------------------------------------------------
  /**
   * @param nonTerminal node in game tree
   * @param action action taken in (non-terminal) state
   * @return new state after transitioning from state via action
   */
  def transition(
    nonTerminal : State,
    action      : Action
    ) : State

  def transitionStateNode(
    nonTerminal : ExtensiveStateNode[State, InformationSet, Action],
    action      : Action)
    : ExtensiveStateNode[State, InformationSet, Action] =
  {
    stateNode(transition(nonTerminal.state, action))
  }

}
