package ao.ai.cfr.util;

import java.util.Random;

/**
 * Fast and good with (2^128 - 1) cycle.
 * See: http://xorshift.di.unimi.it/
 *
 * Not thread safe.
 */
public class XorShift123PlusRandom extends Random
{
    private long seedA;
    private long seedB;
    private boolean initialized;


    public XorShift123PlusRandom() {
        this(System.nanoTime());
    }

    public XorShift123PlusRandom(long seed) {
        super(seed);
        initialized = true;
    }

    @Override
    public void setSeed(long seed) {
        if (initialized) {
            throw new UnsupportedOperationException();
        }

        seedA = murmur3Avalanche(seed);
        seedB = murmur3Avalanche(seedA);
    }


    private long murmur3Avalanche(long x) {
        x ^= x >>> 33;
        x *= 0xff51afd7ed558ccdL;
        x ^= x >>> 33;
        x *= 0xc4ceb9fe1a85ec53L;
        return x ^ (x >>> 33);
    }


    @Override
    public long nextLong() {
        long s1 = seedA;
        long s0 = seedB;
        seedA = s0;
        s1 ^= s1 << 23; // a
        seedB = (s1 ^ s0 ^ (s1 >>> 17) ^ ( s0 >>> 26 ));
        return seedB + s0;
    }

    @Override
    protected int next(int bits) {
        return (int)(nextLong() & ((1L << bits) -1));
    }
}


