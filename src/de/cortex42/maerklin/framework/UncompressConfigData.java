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
            uncompressedBytes[i] = (byte)(bytesDecompressedSoFar.get(i));
        }

        inflater.end();

        return uncompressedBytes;
    }

    //todo crc (Startwert 0xFFFF, Polynom 0x1021 (S. 49)) (=CRC-CCITT (Polynom: x^16+x^12+x^5+1))
    // https://github.com/GBert/railroad/blob/6c3519682aabf6c19438c8a5824db0f7e2f11dd4/can2udp/src/get-cs-config.c
    // https://github.com/GBert/railroad/blob/6c3519682aabf6c19438c8a5824db0f7e2f11dd4/can2udp/src/crc-ccitt.c
}
