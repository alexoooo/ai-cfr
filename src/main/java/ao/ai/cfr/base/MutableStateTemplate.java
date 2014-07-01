package ao.ai.cfr.base;

import ao.ai.cfr.node.ExtensiveNode;

public interface MutableStateTemplate<InformationSet, Action>
{
    ExtensiveNode<InformationSet, Action> node();

    void transition(Action action);
}
