package ao.ai.cfr.state;


import java.util.Arrays;

public class AverageStrategy implements MixedStrategy
{
    private final StrategyAccumulator strategyAccumulator;


    public AverageStrategy(StrategyAccumulator strategyAccumulator) {
        this.strategyAccumulator = strategyAccumulator;
    }


    @Override
    public double[] probabilities(int informationSetIndex, int actionCount) {
        assert actionCount > 0;

        double[] probabilitySums =
                strategyAccumulator.cumulativeStrategy(informationSetIndex);

        if (probabilitySums == null || probabilitySums.length == 0)
        {
            // default; todo: can this be eliminated?
            double[] uniform = new double[actionCount];
            Arrays.fill(uniform, 1.0 / actionCount);
            return uniform;
        }

        assert probabilitySums.length <= actionCount;

        double strategyTotal = 0;
        for (double probabilitySum : probabilitySums) {
            strategyTotal += probabilitySum;
        }

        for (int i = 0; i < probabilitySums.length; i++) {
            probabilitySums[i] /= strategyTotal;
        }

        if (probabilitySums.length == actionCount) {
            return probabilitySums;
        } else {
            return Arrays.copyOf(probabilitySums, actionCount);
        }
    }
}
