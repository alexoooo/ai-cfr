package ao.ai.cfr;

import ao.ai.cfr.node.ExtensiveNode;

/**
 * Extensive form game: http://en.wikipedia.org/wiki/Extensive-form_game.
 * Designed for ease of use of AI writing.
 *
 *
 * @param <State> extensive game state
 * @param <InformationSet> what can be seen during decision points
 * @param <Action> possible actions (both deliberate and chance)
 */
public interface ExtensiveGame<State, InformationSet, Action>
{
    /**
     * @return number of deliberate (aka rational, non-chance) players in the game
     */
    int playerCount();


    /**
     * @return root of the game state tree
     */
    State initialState();


    /**
     * @param state of node of the game tree
     * @return game tree node
     */
    ExtensiveNode<InformationSet, Action> node(State state);


    /**
     * @param state node in game tree
     * @param action action taken in (non-terminal) state
     * @return new state after transitioning from state via action
     */
    State transition(State state, Action action);


    ExtensiveMatch<State, InformationSet, Action> newMatch();
}
