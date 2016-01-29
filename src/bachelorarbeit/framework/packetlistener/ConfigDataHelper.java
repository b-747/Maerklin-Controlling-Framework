package bachelorarbeit.framework.packetlistener;


import java.util.zip.DataFormatException;
import java.util.zip.Inflater;


/**
 * Created by ivo on 12.11.15.
 */
public final class ConfigDataHelper {
    private ConfigDataHelper() {
    }

    /*
    Test with:
        final String compressedDataString= "789CCD944B6E83301445E7AC820D24F51F32F0A05D42A6550794181525E00A08A5BBAF0DC14DD21B1529AAD4919FEFFB9B239E0F766F2BDB95BD79897AD3B4A5ADA3785D95B56D348F5AD39E9472A765F41DEC943AAB8CA6A98C494256C40947174306418874976CD7B85CE3047FEB3EDF75550CCE6AC720AFB9FB9491E445C1A5D9F82EB9ADF5D33676657D8D5E537FBE9ECEDE1E8EBEA737AB6CD08C8FAA1B56B3A9A0EF9338B338D6FBCE0D6EFCE8EBBAD17EBE710A9FBCFE304D375A3FE2A0C890C89128429B14B92512D59C236073B84E1A72E06C1BB81A812A7E065896C29DA9802AA2656B62A1880346ADF83930EA0218058051D7C064D2D01998A9EC6D6098940119496664D4BD9C8C71F049D8EC65D0CD43130E315940D1541D7ED1053C4DD9909205644DEE7F8AD8A389D5838A29154C9C23965C209600C4926BC4D254143362BEEC4AFD865897E56FD66346026F22F00677B8FBBFB40833F8D4F753769B2DCAE144B051408AE14E34BC1126F60FF1FA02218615D60000";

        byte[] decompressed = new byte[0];
        try {
            decompressed = ConfigDataHelper.decompressBytes(DatatypeConverter.parseHexBinary(compressedDataString), 1822);
        } catch (ConfigDataDecompressException e) {
            e.printStackTrace();
        }
        for(int i= 0; i<decompressed.length; i++){
            System.out.print((char)decompressed[i]);
        }
     */

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
     *
     * @param data
     * @return
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

    //alternative
    /*public static int crc_16_ccitt(byte[] data) {
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
    }*/
}
