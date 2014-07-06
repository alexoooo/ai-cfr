package ao.ai.cfr.state;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class ArrayTable
{
    private double[][] values;


    public double[] get(int index) {
        return values[index];
    }


    public void read(InputStream in) throws IOException {
        ObjectInputStream objectIn = new ObjectInputStream(
                new BufferedInputStream(in));

        try {
            values = (double[][]) objectIn.readObject();
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
    }
}
