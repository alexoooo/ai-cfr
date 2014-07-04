package ao.ai.cfr.abs.impl;

import ao.ai.cfr.abs.AbstractionDomain;
import ao.ai.cfr.abs.AbstractionDomainBuilder;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.*;

/**
 * not thread safe
 */
public class DigestDomainBuilder<T extends Serializable> implements AbstractionDomainBuilder<T>
{
    private static final int HASH_SIZE = Digester.MD5_SIZE;

    private final Set<BitSet> population;
    private final MessageDigest digest;


    public DigestDomainBuilder() {
        population = new HashSet<BitSet>();
        digest = Digester.newMd5();
    }


    @Override
    public void add(T view) {
        BitSet bits = Digester.hash(digest, view);
        population.add(bits);
    }



    @Override
    public AbstractionDomain<T> build() {
        int bits = minimumBits();
        SortedSet<BitSet> sorted = sort(bits);
        BitSet flat = flatten(sorted, bits);
        return new DigestDomain<T>(flat, bits, sorted.size(), digest);
    }


    private BitSet flatten(SortedSet<BitSet> sorted, int bits) {
        BitSet flat = new BitSet(bits * sorted.size());

        int offset = 0;
        for (BitSet truncated : sorted) {
            for (int i = 0; i < bits; i++) {
                flat.set(offset + i, truncated.get(i));
            }
            offset += bits;
        }

        return flat;
    }


    private SortedSet<BitSet> sort(final int bits) {
        SortedSet<BitSet> sorted = new TreeSet<BitSet>(Digester.newComparator(bits));

        for (BitSet digest : population) {
            BitSet truncated = Digester.truncate(digest, bits);
            boolean added = sorted.add(truncated);

            if (! added) {
                throw new Error();
            }
        }

        return sorted;
    }


    private int minimumBits() {
        for (int i = 0; i < HASH_SIZE; i++) {
            boolean collides = containsCollision(i);

            if (! collides) {
                return i;
            }
        }

        return -1;
    }


    private boolean containsCollision(int bits) {
        Set<BitSet> sample = new HashSet<BitSet>();

        for (BitSet digest : population) {
            BitSet truncated = Digester.truncate(digest, bits);
            boolean added = sample.add(truncated);

            if (! added) {
                return true;
            }
        }

        return false;
    }
}
