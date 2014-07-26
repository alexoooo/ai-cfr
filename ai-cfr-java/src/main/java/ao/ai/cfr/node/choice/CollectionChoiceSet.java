package ao.ai.cfr.node.choice;

import java.util.Collection;
import java.util.Iterator;

public class CollectionChoiceSet<A> implements ChoiceSet<A> {
    private final Collection<A> actions;


    public CollectionChoiceSet(Collection<A> actions) {
        this.actions = actions;
    }


    @Override
    public int size() {
        return actions.size();
    }

    @Override
    public Iterator<A> iterator() {
        return actions.iterator();
    }


    @Override
    public String toString() {
        return actions.toString();
    }
}
