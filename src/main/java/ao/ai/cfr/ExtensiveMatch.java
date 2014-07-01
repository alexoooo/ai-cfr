package ao.ai.cfr;


import ao.ai.cfr.node.ExtensiveNode;

public interface ExtensiveMatch<State, InformationSet, Action>
{
    State state();

    ExtensiveNode<InformationSet, Action> node();

    void transition(Action action);
}
