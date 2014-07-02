package ao.ai.cfr;

import ao.ai.cfr.node.choice.ChoiceSet;

public interface ExtensivePlayer<InformationSet, Action>
{
    /**
     * @param informationSet observable aspects of the game state
     * @param actions available actions
     * @return one of the available actions
     */
    Action act(InformationSet informationSet, ChoiceSet<Action> actions);
}
