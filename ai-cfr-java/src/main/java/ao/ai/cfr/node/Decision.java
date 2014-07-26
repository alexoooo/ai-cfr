package ao.ai.cfr.node;


import ao.ai.cfr.node.choice.ChoiceSet;

public final class Decision<InformationSet, Action> extends ExtensiveNode<InformationSet, Action>
{
    public static <I, A> Decision<I, A> create(int player, I informationSet, ChoiceSet<A> actions) {
        return new Decision<I, A>(player, informationSet, actions);
    }


    private final int player;
    private final InformationSet informationSet;
    private final ChoiceSet<Action> actions;
    

    Decision(int player, InformationSet informationSet, ChoiceSet<Action> actions) {
        this.player = player;
        this.informationSet = informationSet;
        this.actions = actions;
    }


    public int player() {
        return player;
    }


    public InformationSet informationSet() {
        return informationSet;
    }

    public ChoiceSet<Action> actions() {
        return actions;
    }


    @Override
    public NodeType type() {
        return NodeType.DECISION;
    }



    @Override
    public String toString() {
        return String.format(
                "Decision(%s, %s, %s)",
                player,
                informationSet,
                actions);
    }
}
