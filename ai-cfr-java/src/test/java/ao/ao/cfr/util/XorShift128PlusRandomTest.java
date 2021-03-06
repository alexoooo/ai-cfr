package ao.ao.cfr.util;


import ao.ai.cfr.util.XorShift128PlusRandom;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XorShift128PlusRandomTest
{
    @Test
    public void consecutiveInstancesDiffer() {
        Random a = new XorShift128PlusRandom();
        Random b = new XorShift128PlusRandom();

        assertFalse(a.nextDouble() == b.nextDouble());
    }


    @Test
    public void equalDistribution() {
        Random r = new XorShift128PlusRandom();

        int count = 10000;
        int[] hist = new int[5];
        for (int i = 0; i < count; i++) {
            hist[r.nextInt(5)]++;
        }

        int min = Integer.MAX_VALUE;
        for (int c : hist) {
            min = Math.min(min, c);
        }
        System.out.println(min);

        int threshold = (int) ((double) count / hist.length * 0.9);
        assertTrue(min > threshold);
    }
}
