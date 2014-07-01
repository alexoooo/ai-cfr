package ao.ai.cfr.sample;

import ao.ai.cfr.ExtensivePlayer;
import ao.ai.cfr.node.choice.ChoiceSet;

import java.util.Random;

public class RandomPlayer<I, A> implements ExtensivePlayer<I, A> {
    private final Random random;

    public RandomPlayer(Random random) {
        this.random = random;
    }


    @Override
    public A act(I i, ChoiceSet<A> as) {
        A maxChoice = null;
        double maxChoiceWeight = 0;

        for (A choice : as) {
            double weight = random.nextDouble();
            if (maxChoiceWeight < weight) {
                maxChoice = choice;
                maxChoiceWeight = weight;
            }
        }

        return maxChoice;
    }
}
