package ao.ai.cfr.abs.impl;

import java.io.*;
import java.util.BitSet;

public enum DigestDomains
{;
    public static void write(DigestDomain<?> domain, OutputStream out) {
        try {
            writeChecked(domain, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void writeChecked(DigestDomain<?> domain, OutputStream out) throws IOException {
        ObjectOutputStream objectOut = new ObjectOutputStream(out);

        objectOut.writeInt(domain.bits);
        objectOut.writeInt(domain.size);
        objectOut.writeObject(domain.domain);
    }


    public static <T extends Serializable> DigestDomain<T> read(byte[] in) {
        return read(new ByteArrayInputStream(in));
    }
    public static <T extends Serializable> DigestDomain<T> read(InputStream in) {
        try {
            return readChecked(in);
        } catch (IOException e) {
            throw new Error(e);
        }
    }
    public static <T extends Serializable> DigestDomain<T> readChecked(InputStream in) throws IOException {
        ObjectInputStream objectIn = new ObjectInputStream(in);
        try {
            int bits = objectIn.readInt();
            int size = objectIn.readInt();
            BitSet domain = (BitSet) objectIn.readObject();

            return new DigestDomain<T>(domain, bits, size);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
    }
}
