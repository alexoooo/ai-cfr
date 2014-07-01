package ao.ai.cfr.node;


import ao.ai.cfr.node.outcome.OutcomeSet;


public final class Chance<InformationSet, Action> extends ExtensiveNode<InformationSet, Action>
{
    public static <I, A> Chance<I, A> create(OutcomeSet<A> outcomes) {
        return new Chance<I, A>(outcomes);
    }


    private final OutcomeSet<Action> outcomes;


    Chance(OutcomeSet<Action> outcomes) {
        this.outcomes = outcomes;
    }


    public OutcomeSet<Action> outcomes() {
        return outcomes;
    }


    @Override
    public NodeType type() {
        return NodeType.CHANCE;
    }
}
