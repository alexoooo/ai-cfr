package ao.ai.cfr.node;

import java.util.Arrays;

/**
 *
 */
public final class Terminal<InformationSet, Action> extends ExtensiveNode<InformationSet, Action>
{
    public static <I, A> Terminal<I, A> create(double... payoffs) {
        return new Terminal<I, A>(payoffs.clone());
    }


    private final double[] payoffs;


    Terminal(double[] payoffs) {
        this.payoffs = payoffs;
    }


    public double payoff(int player) {
        return payoffs[player];
    }

    public double[] payoffs() {
        return payoffs.clone();
    }


    @Override
    public NodeType type() {
        return NodeType.TERMINAL;
    }


    @Override
    public String toString() {
        return Arrays.toString(payoffs);
    }
}
