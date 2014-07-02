package ao.ai.cfr.node.outcome;


import java.util.Random;

public interface OutcomeSet<A> extends Iterable<A>
{
    double probability(A action);

    A sample(Random random);
}
