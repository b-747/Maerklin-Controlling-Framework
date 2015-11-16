package de.cortex42.maerklin.framework;

import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * Created by ivo on 12.11.15.
 */
public final class UncompressConfigData {
    private UncompressConfigData(){}

    //zlib inflate
    public static byte[] uncompressBytes(byte[] bytes) throws DataFormatException {
        byte[] uncompressedBytes;

        Inflater inflater = new Inflater();

        inflater.setInput(
                bytes,
                0,
                bytes.length
        );

        int numberOfBytesDecompressedCounter = 0;
        ArrayList<Byte> bytesDecompressedSoFar = new ArrayList<Byte>();

        while(!inflater.needsInput()){ //returns true if no data remains in the input buffer
            byte[] bytesDecompressedBuffer = new byte[bytes.length];

            int numberOfBytesDecompressedThisTime = inflater.inflate(bytesDecompressedBuffer);

            numberOfBytesDecompressedCounter += numberOfBytesDecompressedThisTime;

            for(int i = 0; i < numberOfBytesDecompressedThisTime; i++){
                bytesDecompressedSoFar.add(bytesDecompressedBuffer[i]);
            }
        }

        uncompressedBytes = new byte[bytesDecompressedSoFar.size()];
        for(int i = 0; i<uncompressedBytes.length; i++){
            uncompressedBytes[i] = bytesDecompressedSoFar.get(i);
        }

        inflater.end();

        return uncompressedBytes;
    }


    // https://github.com/GBert/railroad/blob/6c3519682aabf6c19438c8a5824db0f7e2f11dd4/can2udp/src/get-cs-config.c
    // https://github.com/GBert/railroad/blob/6c3519682aabf6c19438c8a5824db0f7e2f11dd4/can2udp/src/crc-ccitt.c

    /**
     * Calculates the crc over all config data packets (including the last with the null bytes at the end)
     * (see page 49)
     *
     * @param data
     * @return
     */
    public static int calcCRC(byte[] data) {
        final int POLY = 0x1021;
        final int START_VALUE = 0xFFFF;

        int current_crc_value = START_VALUE;

        for (int i = 0; i < data.length; i++) {
            // create the crc "dividend" for polynomial arithmetic (binary arithmetic with no carries)
            current_crc_value ^= data[i] & 0xFF; //line 2 in protocol

            // "Divide" the poly into the dividend using CRC XOR subtraction CRC_acc holds the
            // "remainder" of each divide. Only complete this division for 8 bits since input is 1 byte
            for (int j = 0; j < 8; j++) {
                // Check if the MSB is set (if MSB is 1, then the POLY can "divide" into the "dividend")
                if ((current_crc_value & 0x8000) == 0x8000) { //0x8000 = 1000 0000 0000 0000
                    // if so, shift the CRC value, and XOR "subtract" the poly
                    current_crc_value = current_crc_value << 1;
                    current_crc_value ^= POLY;
                } else {
                    // if not, just shift the CRC value
                    current_crc_value = current_crc_value << 1;
                }
            }
        }

        return current_crc_value & 0xFFFF;
    }
}
