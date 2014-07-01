package ao.ai.cfr.base;

import ao.ai.cfr.ExtensiveMatch;
import ao.ai.cfr.node.ExtensiveNode;

public class MutableStateMatch<I, A, S extends MutableStateTemplate<I, A>> implements ExtensiveMatch<S, I, A>
{
    private final S state;
    private ExtensiveNode<I, A> node;


    public MutableStateMatch(S state) {
        this.state = state;
    }


    @Override
    public S state() {
        return state;
    }

    @Override
    public ExtensiveNode<I, A> node() {
        if (node == null) {
            node = state.node();
        }
        return node;
    }

    @Override
    public void transition(A action) {
        state.transition(action);
        node = null;
    }
}
