package ao.ai.cfr.node;


public abstract class ExtensiveNode<InformationSet, Action>
{
    ExtensiveNode() {}

    public abstract NodeType type();


    public Decision<InformationSet, Action> asDecision() {
        return (Decision<InformationSet, Action>) this;
    }

    public Chance<InformationSet, Action> asChance() {
        return (Chance<InformationSet, Action>) this;
    }

    public Terminal<InformationSet, Action> asTerminal() {
        return (Terminal<InformationSet, Action>) this;
    }
}
