package ao.ai.cfr.base;


import ao.ai.cfr.ExtensiveGame;
import ao.ai.cfr.ExtensiveMatch;
import ao.ai.cfr.node.ExtensiveNode;

public class GameMatch<S, I, A> implements ExtensiveMatch<S, I, A>
{
    private final ExtensiveGame<S, I, A> game;

    private S state;
    private ExtensiveNode<I, A> node;


    public GameMatch(ExtensiveGame<S, I, A> game) {
        this.game = game;

        setState(game.initialState());
    }


    public void setState(S state) {
        this.state = state;
    }


    @Override
    public S state() {
        return state;
    }

    @Override
    public ExtensiveNode<I, A> node() {
        if (node == null) {
            node = game.node(state);
        }
        return node;
    }

    @Override
    public void transition(A action) {
        state = game.transition(state, action);
        node = null;
    }
}
