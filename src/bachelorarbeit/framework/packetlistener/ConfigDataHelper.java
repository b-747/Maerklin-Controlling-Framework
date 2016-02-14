package bachelorarbeit.framework.packetlistener;


import java.util.zip.DataFormatException;
import java.util.zip.Inflater;


/**
 * Created by ivo on 12.11.15.
 */
public final class ConfigDataHelper {
    private ConfigDataHelper() {
    }

    public static byte[] decompressBytes(final byte[] compressedBytes, final int decompressedBytesLength) throws ConfigDataDecompressException {
        final byte[] decompressedBytes = new byte[decompressedBytesLength];

        final Inflater inflater = new Inflater();
        inflater.setInput(compressedBytes);

        try {
            inflater.inflate(decompressedBytes);
        } catch (final DataFormatException e) {
            throw new ConfigDataDecompressException(e);
        } finally {
            inflater.end();
        }

        return decompressedBytes;
    }

    /**
     * Calculates the CRC over all config data packets (including the last with the null bytes at the end)
     * (see page 49)
     */
    public static int calcCRC(final byte[] data) {
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
}
