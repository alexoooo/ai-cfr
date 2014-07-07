package ao.ai.cfr.state;


import java.io.*;

public class ArrayTable
{
    private double[][] values;


    public double[] get(int index) {
        return values[index];
    }



    public void read(byte[] in) {
        try {
            read(new ByteArrayInputStream(in));
        } catch (IOException e) {
            throw new Error(e);
        }
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
