package ao.ai.cfr.state;


public class ArrayStrategyAccumulator implements StrategyAccumulator
{
    private final ArrayTable arrayTable;


    public ArrayStrategyAccumulator(ArrayTable arrayTable) {
        this.arrayTable = arrayTable;
    }


    @Override
    public double[] cumulativeStrategy(int informationSetIndex) {
        return arrayTable.get(informationSetIndex);
    }
}
