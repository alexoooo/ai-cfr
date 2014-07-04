package ao.ai.cfr.abs.impl;

import ao.ai.cfr.abs.AbstractionDomain;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.BitSet;


class DigestDomain<T extends Serializable> implements AbstractionDomain<T>
{
    private final BitSet domain;
    private final int bits;
    private final int size;
    private final MessageDigest digest;


    public DigestDomain(BitSet domain, int bits, int size, MessageDigest digest) {
        this.domain = domain;
        this.bits = bits;
        this.size = size;
        this.digest = digest;
    }


    @Override
    public int size() {
        return size;
    }


    @Override
    public int indexOf(T view) {
        BitSet target = Digester.hash(digest, view);

        int index = binarySearch(target);
        if (index < 0) {
            throw new IllegalArgumentException("unknown: " + view);
        }

        return index;
    }


    private int binarySearch(BitSet key) {
        int low = 0;
        int high = size - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;

            int midOffset = mid * bits;
            int midComparison = Digester.compare(domain, midOffset, bits, key);

            if (midComparison < 0)
                low = mid + 1;
            else if (midComparison > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }
}
