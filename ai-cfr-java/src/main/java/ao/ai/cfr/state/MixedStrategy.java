package ao.ai.cfr.state;

/**
 *
 */
public interface MixedStrategy
{
    double[] probabilities(int informationSetIndex, int actionCount);
}
