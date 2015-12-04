package de.cortex42.maerklin.framework;

import com.jcraft.jzlib.Inflater;
import com.jcraft.jzlib.JZlib;


/**
 * Created by ivo on 12.11.15.
 */
public final class DecompressConfigData {
    private DecompressConfigData() {
    }

    //zlib inflate
    public static byte[] decompressBytes(byte[] compressedBytes, int decompressedBytesLength) {
        byte[] decompressedBytes = new byte[decompressedBytesLength];

        Inflater inflater = new Inflater();
        inflater.setInput(compressedBytes);
        inflater.setOutput(decompressedBytes);

        inflater.init();

        while (inflater.total_in < decompressedBytes.length && inflater.total_in < compressedBytes.length) {

            if (inflater.inflate(JZlib.Z_NO_FLUSH) == JZlib.Z_STREAM_END) {
                break;
            }

        }

        inflater.end();

        return decompressedBytes;
    }

    /**
     * Calculates the calcCRC over all config data packets (including the last with the null bytes at the end)
     * (see page 49)
     *
     * @param data
     * @return
     */
    public static int calcCRC(byte[] data) {
        final int POLY = 0x1021;
        final int START_VALUE = 0xFFFF;

        int crc = START_VALUE;

        for (int i = 0; i < data.length; i++) {

            crc = crc ^ ((data[i] & 0xFF) << 8);

            for (int j = 0; j < 8; j++) {
                if ((crc & 0x8000) == 0x8000) {
                    crc = crc << 1;
                    crc = crc ^ POLY;
                } else {
                    crc = crc << 1;
                }
            }
        }

        return crc & 0xFFFF;
    }

    //alternative
    public static int crc_16_ccitt(byte[] data) {
        int crc = 0xFFFF; // initial value
        int polynomial = 0x1021; // 0001 0000 0010 0001 (0, 5, 12)

        boolean bit, c15;
        for (int b = 0; b < data.length; b++) {
            for (int i = 0; i < 8; i++) {
                bit = ((data[b] >> (7 - i) & 1) == 1);
                c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit)
                    crc ^= polynomial;
            }
        }
        crc &= 0xffff;

        return crc;
    }
}
