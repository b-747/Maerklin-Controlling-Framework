package de.cortex42.maerklin.framework;
/**
 * Created by ivo on 16.10.15.
 */

/**
 * Describes a CAN message.
 *
 * @see http://medienpdb.maerklin.de/digital22008/files/cs2CAN-Protokoll-2_0.pdf (chapter 1.1)
 */
public class CANPacket {

    public static final int CAN_PACKET_SIZE = 13;
    public static final int HASH_SIZE = 2;
    public static final int DATA_SIZE = 8;

    private final byte priority;
    private final byte command;
    private final byte[] hash;
    private final byte dlc;
    private final byte[] data;

    /**
     * @param priority
     * @param command
     * @param hash     2 bytes
     * @param dlc
     * @param data     8 bytes
     */
    public CANPacket(final byte priority, final byte command, final byte[] hash, final byte dlc, final byte[] data) {
        this.priority = priority;

        this.command = command;

        if (hash.length != HASH_SIZE) {
            throw new IllegalArgumentException("hash.length != 2: " + hash.length);
        }
        this.hash = hash;


        this.dlc = dlc;

        if (data.length != DATA_SIZE) {
            throw new IllegalArgumentException("data.length != 8: " + data.length);
        }
        this.data = data;

    }

    public CANPacket(final byte[] bytes) {
        if (bytes.length != CAN_PACKET_SIZE) {
            throw new IllegalArgumentException("bytes.length != 13: " + bytes.length);
        }

        this.priority = bytes[0];

        this.command = bytes[1];

        this.hash = new byte[]{bytes[2], bytes[3]};

        this.dlc = bytes[4];

        this.data = new byte[]{
                bytes[5],
                bytes[6],
                bytes[7],
                bytes[8],
                bytes[9],
                bytes[10],
                bytes[11],
                bytes[12]
        };
    }

    public byte getPriority() {
        return priority;
    }

    public byte getCommand() {
        return command;
    }

    public byte[] getHash() {
        return hash;
    }

    public byte getDlc() {
        return dlc;
    }

    public byte[] getData() {
        return data;
    }

    public int getID() {
        return ((data[0] & 0xFF) << 24)
                | ((data[1] & 0xFF) << 16)
                | ((data[2] & 0xFF) << 8)
                | (data[3] & 0xFF);
    }

    /**
     * @return all bytes together (13 bytes)
     */
    public byte[] getBytes() {
        byte[] bytes = new byte[13];

        bytes[0] = priority;
        bytes[1] = command;
        System.arraycopy(hash, 0, bytes, 2, hash.length); //bytes[2], bytes[3]
        bytes[4] = dlc;
        System.arraycopy(data, 0, bytes, 5, data.length); //bytes[5 - 12]

        return bytes;
    }
}
