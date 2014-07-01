package ao.ai.cfr.node.outcome;

import java.util.*;

public class UniformOutcomeSet<A> implements OutcomeSet<A>
{
    private final List<A> outcomes;
    private final double uniformProbability;


    public UniformOutcomeSet(A... outcomes) {
        this(Arrays.asList(outcomes));
    }
    public UniformOutcomeSet(Collection<A> outcomes) {
        this(new ArrayList<A>(outcomes));
    }
    public UniformOutcomeSet(List<A> outcomes) {
        this.outcomes = outcomes;

        uniformProbability = 1.0 / outcomes.size();
    }


    @Override
    public double probability(A action) {
        return uniformProbability;
    }

    @Override
    public A sample(Random random) {
        return outcomes.get(random.nextInt(outcomes.size()));
    }

    @Override
    public Iterator<A> iterator() {
        return outcomes.iterator();
    }
}
