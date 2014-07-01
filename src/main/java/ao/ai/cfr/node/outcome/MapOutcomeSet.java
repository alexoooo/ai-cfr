package ao.ai.cfr.node.outcome;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class MapOutcomeSet<A> implements OutcomeSet<A>
{
    private final Map<A, Double> outcomes;


    public MapOutcomeSet(Map<A, Double> outcomes) {
        this.outcomes = outcomes;
    }


    @Override
    public double probability(A action) {
        return outcomes.get(action);
    }


    @Override
    public A sample(Random random) {
        double maxWeight = 0;
        A maxWeightAction = null;

        for (Map.Entry<A, Double> outcome : outcomes.entrySet()) {
            double weight = random.nextDouble() * outcome.getValue();

            if (maxWeight < weight) {
                maxWeightAction = outcome.getKey();
                maxWeight = weight;
            }
        }

        return maxWeightAction;
    }


    @Override
    public Iterator<A> iterator() {
        return outcomes.keySet().iterator();
    }
}
