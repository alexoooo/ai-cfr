package ao.ai.cfr.base;

import ao.ai.cfr.ExtensiveMatch;
import ao.ai.cfr.node.ExtensiveNode;

public abstract class AbstractMutableGame<I, A, S extends MutableStateTemplate<I, A>> extends AbstractGame<S, I, A>
{
    public AbstractMutableGame(int playerCount, S initialState) {
        super(playerCount, initialState);
    }

    @Override
    public ExtensiveNode<I, A> node(S state) {
        return state.node();
    }

    @Override
    public S transition(S state, A action) {
        S newState = copy(state);
        newState.transition(action);
        return newState;
    }

    @Override
    public ExtensiveMatch<S, I, A> newMatch() {
        return new MutableStateMatch<I, A, S>(initialState());
    }

    protected abstract S copy(S state);
}
