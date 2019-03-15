package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PartitionKeyMap {

    public static String getPartitonId(String partitionKey, int totalPartitionCount) {
        long hash = getHash(partitionKey);
        return String.valueOf(getPartition(totalPartitionCount, hash));
    }

    public static int getPartition(int total, Long hashKey) {
        long avg = Long.MAX_VALUE / total;

        int activePartitionIndex = (int) (hashKey / avg);
        long mod = hashKey % avg;

        if (activePartitionIndex > 0 && mod == 0) {
            activePartitionIndex = activePartitionIndex - 1;
        }

        return activePartitionIndex;
    }

    public static long getHash(String value) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return -1;
        }

        md.update(value.getBytes());
        byte byteData[] = md.digest();

        return getLong(byteData) & Long.MAX_VALUE;
    }

    protected static final long getLong(final byte[] array) {
        final int SHIFTBITS = 8;
        final int NUMBER_HASH_BYTES = 8;

        int totalBytesToConvert = (array.length > NUMBER_HASH_BYTES) ? NUMBER_HASH_BYTES : array.length;

        long value = 0;
        for (int i = 0; i < totalBytesToConvert; i++) {
            value = ((value << SHIFTBITS) | (array[i] & 0xFF));
        }
        return value;
    }
}
