package ao.ai.cfr.abs.impl;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Comparator;

public enum Digester
{;
    public static final int MD5_SIZE = 128;


    public static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e);
        }
    }


    public static BitSet truncate(BitSet value, int bits) {
        BitSet truncated = new BitSet();

        for (int i = 0; i < bits; i++) {
            boolean bit = value.get(i);
            truncated.set(i, bit);
        }

        return truncated;
    }


    public static BitSet hash(MessageDigest digest, Serializable value) {
        byte[] bytes = hashBytes(digest, value);
        return BitSet.valueOf(bytes);
    }

    private static byte[] hashBytes(MessageDigest digest, Serializable value) {
        try {
            return hashChecked(digest, value);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    private static byte[] hashChecked(MessageDigest digest, Serializable value) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ObjectOutputStream objectOut = new ObjectOutputStream(out);
        objectOut.writeObject(value);
        objectOut.close();

        byte[] raw = out.toByteArray();

        digest.update(raw);

        return digest.digest();
    }


    public static Comparator<BitSet> newComparator(final int bits) {
        return new Comparator<BitSet>() {
            @Override public int compare(BitSet a, BitSet b) {
                for (int i = 0; i < bits; i++) {
                    int comparison = Boolean.compare(a.get(i), b.get(i));
                    if (comparison != 0) {
                        return comparison;
                    }
                }
                return 0;
            }};
    }

    public static int compare(BitSet flat, int offset, int bits, BitSet compareTo) {
        for (int i = 0, index = offset; i < bits; i++, index++) {
            boolean a = flat.get(index);
            boolean b = compareTo.get(i);

            int comparison = Boolean.compare(a, b);
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }
}
