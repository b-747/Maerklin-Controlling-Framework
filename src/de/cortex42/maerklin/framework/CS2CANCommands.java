package de.cortex42.maerklin.framework;

/**
 * Created by ivo on 19.10.15.
 */
public final class CS2CANCommands {
    //todo ist eine konkrete Fabrik (=Factory) (Patterns kompakt, S.28)
    /**
     * BIG ENDIAN
     */
    /*---PRIORITY---*/
    public static final byte PRIORITY = 0x00;
    /*---HASH---*/
    /* //todo
    *
    * Hash 0x0300: wird gebildet aus UID 0x00000000 oder 0xFFFFFFFF (sollte also mit real existierender Hardware nicht kollidieren)
    *
    public static int calcHash(int uid) {
        return (((uid >> 16) ^ (uid & 0xFFFF)) & 0xFF7F) | 0x0300;
       (upper 16 bits XOR lower 16 bits) AND Bit7=0 OR Bit8/9=1
    }
    */
    public static final byte[] HASH = new byte[]{0x03, 0x00};
    /*---RESPONSE---*/
    public static final byte RESPONSE = 0x01;
    /*---COMMANDS---*/
    public static final byte BOOTLOADER_GO = 0x36;
    public static final byte PING = 0x30;
    public static final byte DIRECTION = 0x0A;
    public static final byte FUNCTION = 0x0C;
    public static final byte SYSTEM = 0x00;
    public static final byte VELOCITY = 0x08;
    public static final byte EQUIPMENT = 0x16;
    public static final byte DISCOVERY = 0x02; //only master should send this!
    public static final byte MFX_BIND = 0x04; //only master should send this!
    public static final byte S88_EVENT = 0x22;
    public static final byte REQUEST_CONFIG_DATA = 0x40;
    public static final byte GET_CONFIG_DATA_STREAM = 0x42;
    public static final byte READ_CONFIG = 0x0E;
    /*---SYSTEM SUB-CMDs---*/
    public static final byte SYSTEM_STOP_SUBCMD = 0x00;
    public static final byte SYSTEM_GO_SUBCMD = 0x01;
    public static final byte SYSTEM_RAIL_UNLOCK_SUBCMD = 0x08; //"Gleisprotokoll freischalten"
    public static final byte SYSTEM_MFX_REGISTRATION_COUNTER_SUBCMD = 0x09; //"System MFX Neuanmeldezähler setzen"
    /*---BOOTLOADER---*/
    public static final byte BOOTLOADER_MAGIC_BYTE = 0x11;
    /*---DLCs---*/
    public static final byte BOOTLOADER_DLC = 0x05;
    public static final byte PING_QUERY_DLC = 0x00;
    public static final byte PING_RESPONSE_DLC = 0x08;
    public static final byte DIRECTION_QUERY_DLC = 0x04;
    public static final byte DIRECTION_SET_DLC = 0x05;
    public static final byte FUNCTION_QUERY_DLC = 0x05;
    public static final byte FUNCTION_SET_DLC = 0x06;
    public static final byte SYSTEM_STOP_AND_GO_DLC = 0x05;
    public static final byte SYSTEM_RAIL_UNLOCK_DLC = 0x06;
    public static final byte SYSTEM_MFX_REGISTRATION_COUNTER_DLC = 0x07;
    public static final byte VELOCITY_QUERY_DLC = 0x04;
    public static final byte VELOCITY_SET_DLC = 0x06;
    public static final byte EQUIPMENT_DLC = 0x06;
    public static final byte DISCOVERY_DISCOVER_ALL_DLC = 0x00;
    public static final byte DISCOVERY_DISCOVER_PROTOCOL_DLC = 0x01;
    public static final byte DISCOVERY_POSITIVE_ANSWER_DLC = 0x05; //answers to previous dlc (0x01)
    public static final byte DISCOVERY_NEGATIVE_ANSWER_DLC = 0x00;
    public static final byte MFX_BIND_DLC = 0x06;
    public static final byte S88_EVENT_QUERY_DLC = 0x04;
    public static final byte S88_EVENT_RESPONSE_DLC = 0x08;
    public static final byte REQUEST_CONFIG_DATA_DLC = 0x08;
    public static final byte GET_CONFIG_DATA_STREAM_REQUEST_RESPONSE_DLC = 0x06;
    public static final byte GET_CONFIG_DATA_STREAM_FIRST_PACKET_CONFIG_CHANGED_DLC = 0x07;
    public static final byte GET_CONFIG_DATA_STREAM_PACKET_DLC = 0x08;
    public static final byte READ_CONFIG_DLC = 0x07;
    public static final byte READ_CONFIG_SUCCESS_DLC = 0x07;
    public static final byte READ_CONFIG_FAILED_DLC = 0x06;
    /*---SYSTEM_RAIL_UNLOCK_PARAM---*/
    public static final byte SYSTEM_RAIL_UNLOCK_PARAM = 0b00000111; //see protocol 2.9 (unlocks mm2, mfx, dcc)
    /*---SYSTEM_MFX_REGISTRATION_COUNTER_VALUE---*/
    public static final byte[] SYSTEM_MFX_REGISTRATION_COUNTER_VALUE = new byte[]{0x00, 0x02};
    /*---DISCOVERY_PROTOCOL_IDENTIFIER---*/
    public static final byte DISCOVERY_DISCOVER_MFX = 0x20; //"Voller mfx Discoveryzyklus" (S. 27)
    public static final byte DISCOVERY_DISCOVER_MM2 = 0x21;
    /**
     * Public flags for the commands.
     */
    /*---DIRECTION---*/
    public static final byte DIRECTION_MAINTAIN = 0x00;
    public static final byte DIRECTION_FORWARD = 0x01;
    public static final byte DIRECTION_BACKWARD = 0x02;
    public static final byte DIRECTION_TOGGLE = 0x03;
    /*---FUNCTION---*/
    public static final byte FUNCTION_OFF = 0x00;
    public static final byte FUNCTION_ON = 0x01;
    /*---EQUIPMENT_POSITION---*/
    public static final byte EQUIPMENT_POSITION_OFF = 0x00;
    public static final byte EQUIPMENT_POSITION_ON = 0x01;
    /*---EQUIPMENT POWER---*/
    public static final byte EQUIPMENT_POWER_OFF = 0x00;
    public static final byte EQUIPMENT_POWER_ON = 0x01;

    private CS2CANCommands() {
        /* ... */
    }

    //todo wait 400ms after sending bootloader go!
    public static CANPacket bootloaderGo() {
        return new CANPacket(
                PRIORITY,
                BOOTLOADER_GO,
                HASH,
                BOOTLOADER_DLC,
                new byte[]{
                        0x00,
                        0x00,
                        0x00,
                        0x00,
                        BOOTLOADER_MAGIC_BYTE,
                        0x00,
                        0x00,
                        0x00
                }
        );
    }

    public static CANPacket ping() {
        return new CANPacket(
                PRIORITY,
                PING,
                HASH,
                PING_QUERY_DLC, //query bus participants
                new byte[8] //all 0x00
        );
    }

    /**
     * //todo
     *
     * @param locId consists of the basic address (defines the protocol) and the number of the loc (see page 8 for details).
     *              (example: MFX loc with number 3: MFX=0x4000 + 3 = 0x4003; upper two bytes are always 0!)
     * @return
     */
    public static CANPacket queryDirection(final int locId) {
        return new CANPacket(
                PRIORITY,
                DIRECTION,
                HASH,
                DIRECTION_QUERY_DLC,
                new byte[]{
                        (byte) ((locId >> 24) & 0xFF),
                        (byte) ((locId >> 16) & 0xFF),
                        (byte) ((locId >> 8) & 0xFF),
                        (byte) (locId & 0xFF),
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                }
        );
    }

    public static CANPacket setDirection(final int locId, final int direction) {
        if (direction > 3 || direction < 0) {
            throw new IllegalArgumentException("direction must be between 0 and 3. (Error: " + direction + ").");
        }

        return new CANPacket(
                PRIORITY,
                DIRECTION,
                HASH,
                DIRECTION_SET_DLC,
                new byte[]{
                        (byte) ((locId >> 24) & 0xFF),
                        (byte) ((locId >> 16) & 0xFF),
                        (byte) ((locId >> 8) & 0xFF),
                        (byte) (locId & 0xFF),
                        (byte) (direction & 0xFF),
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                }
        );
    }

    public static CANPacket queryFunction(final int locId, final int function) {
        if (function > 31 || function < 0) {
            throw new IllegalArgumentException("function must be between 0 and 31. (Error: " + function + ").");
        }

        return new CANPacket(
                PRIORITY,
                FUNCTION,
                HASH,
                FUNCTION_QUERY_DLC,
                new byte[]{
                        (byte) ((locId >> 24) & 0xFF),
                        (byte) ((locId >> 16) & 0xFF),
                        (byte) ((locId >> 8) & 0xFF),
                        (byte) (locId & 0xFF),
                        (byte) (function & 0xFF),
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                }
        );
    }

    public static CANPacket toggleFunction(final int locId, final int function, final int toggle) {
        if (function > 31 || function < 0) {
            throw new IllegalArgumentException("function must be between 0 and 31. (Error: " + function + ").");
        }

        if (toggle > 1 || toggle < 0) {
            throw new IllegalArgumentException("toggle must be either 0 or 1. (Error: " + toggle + ").");
        }

        return new CANPacket(
                PRIORITY,
                FUNCTION,
                HASH,
                FUNCTION_SET_DLC,
                new byte[]{
                        (byte) ((locId >> 24) & 0xFF),
                        (byte) ((locId >> 16) & 0xFF),
                        (byte) ((locId >> 8) & 0xFF),
                        (byte) (locId & 0xFF),
                        (byte) (function & 0xFF),
                        (byte) (toggle & 0xFF),
                        (byte) 0x00,
                        (byte) 0x00
                }
        );
    }

    public static CANPacket stop() {
        return new CANPacket(
                PRIORITY,
                SYSTEM,
                HASH,
                SYSTEM_STOP_AND_GO_DLC,
                new byte[]{
                        (byte) 0x00, //target all (UID = 0x00000000)
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        SYSTEM_STOP_SUBCMD,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                }
        );
    }

    public static CANPacket stop(final int uid) {
        return new CANPacket(
                PRIORITY,
                SYSTEM,
                HASH,
                SYSTEM_STOP_AND_GO_DLC,
                new byte[]{
                        (byte) ((uid >> 24) & 0xFF),
                        (byte) ((uid >> 16) & 0xFF),
                        (byte) ((uid >> 8) & 0xFF),
                        (byte) (uid & 0xFF),
                        SYSTEM_STOP_SUBCMD,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                }
        );
    }

    public static CANPacket go() {
        return new CANPacket(
                PRIORITY,
                SYSTEM,
                HASH,
                SYSTEM_STOP_AND_GO_DLC,
                new byte[]{
                        (byte) 0x00, //target all (UID = 0x00000000)
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        SYSTEM_GO_SUBCMD,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                }
        );
    }

    public static CANPacket go(final int uid) {
        return new CANPacket(
                PRIORITY,
                SYSTEM,
                HASH,
                SYSTEM_STOP_AND_GO_DLC,
                new byte[]{
                        (byte) ((uid >> 24) & 0xFF),
                        (byte) ((uid >> 16) & 0xFF),
                        (byte) ((uid >> 8) & 0xFF),
                        (byte) (uid & 0xFF),
                        SYSTEM_GO_SUBCMD,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                }
        );
    }

    public static CANPacket unlockRail() {
        return new CANPacket(
                PRIORITY,
                SYSTEM,
                HASH,
                SYSTEM_RAIL_UNLOCK_DLC,
                new byte[]{
                        (byte) 0x00, //target all (UID = 0x00000000)
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        SYSTEM_RAIL_UNLOCK_SUBCMD,
                        SYSTEM_RAIL_UNLOCK_PARAM, //unlock mfx, mm2, dcc
                        (byte) 0x00,
                        (byte) 0x00
                }
        );
    }

    public static CANPacket newRegistration() {
        return new CANPacket(
                PRIORITY,
                SYSTEM,
                HASH,
                SYSTEM_MFX_REGISTRATION_COUNTER_DLC,
                new byte[]{
                        (byte) 0x00, //target all (UID = 0x00000000)
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        SYSTEM_MFX_REGISTRATION_COUNTER_SUBCMD,
                        SYSTEM_MFX_REGISTRATION_COUNTER_VALUE[0],
                        SYSTEM_MFX_REGISTRATION_COUNTER_VALUE[1],
                        (byte) 0x00
                }
        );
    }

    public static CANPacket queryVelocity(final int locId) {
        return new CANPacket(
                PRIORITY,
                VELOCITY,
                HASH,
                VELOCITY_QUERY_DLC,
                new byte[]{
                        (byte) ((locId >> 24) & 0xFF),
                        (byte) ((locId >> 16) & 0xFF),
                        (byte) ((locId >> 8) & 0xFF),
                        (byte) (locId & 0xFF),
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                }
        );
    }

    public static CANPacket setVelocity(final int locId, final int velocity) {
        if (velocity < 0 || velocity > 1023) {
            throw new IllegalArgumentException("velocity must be between 0 and 1023. (Error: " + velocity + ").");
        }

        return new CANPacket(
                PRIORITY,
                VELOCITY,
                HASH,
                VELOCITY_SET_DLC,
                new byte[]{
                        (byte) ((locId >> 24) & 0xFF),
                        (byte) ((locId >> 16) & 0xFF),
                        (byte) ((locId >> 8) & 0xFF),
                        (byte) (locId & 0xFF),
                        //max velocity = 0x03E8 = 1000
                        (byte) ((velocity >> 8) & 0xFF),
                        (byte) (velocity & 0xFF),
                        (byte) 0x00,
                        (byte) 0x00
                }
        );
    }

    /**
     * //todo
     *
     * @param locId
     * @param position    1 = straight
     * @param powerToggle
     * @return
     */
    public static CANPacket toggleEquipment(final int locId, final int position, final int powerToggle) {
        return new CANPacket(
                PRIORITY,
                EQUIPMENT,
                HASH,
                EQUIPMENT_DLC,
                new byte[]{
                        (byte) ((locId >> 24) & 0xFF),
                        (byte) ((locId >> 16) & 0xFF),
                        (byte) ((locId >> 8) & 0xFF),
                        (byte) (locId & 0xFF),
                        (byte) (position & 0xFF),
                        (byte) (powerToggle & 0xFF),
                        (byte) 0x00,
                        (byte) 0x00
                }
        );
    }

    /**
     * Discovery of every loc with every protocol.
     * Should only be sent by the master!
     *
     * @return
     */
    public static CANPacket discoverAllLocs() {
        return new CANPacket(
                PRIORITY,
                DISCOVERY,
                HASH,
                DISCOVERY_DISCOVER_ALL_DLC,
                new byte[8]
        );
    }

    /**
     * Discovery of locs with a specific protocol.
     * Should only be sent by the master!
     *
     * @param protocol protocol id (see page 26 of the CS2 protocol)
     * @return
     */
    public static CANPacket discoverSpecificLocs(final int protocol) {
        if (protocol < 0 || protocol > 96) {
            throw new IllegalArgumentException("protocol must be between 0 and 96 (Error: " + protocol + ").");
        }

        return new CANPacket(
                PRIORITY,
                DISCOVERY,
                HASH,
                DISCOVERY_DISCOVER_PROTOCOL_DLC,
                new byte[]{
                        (byte) (protocol & 0xFF),
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                }
        );
    }

    /**
     * Assigns a sid to a mfx decoder.
     * Should only be sent by the master!
     *
     * @param uid
     * @param sid
     * @return
     */
    public static CANPacket mfxBind(final int uid, final int sid) {
        return new CANPacket(
                PRIORITY,
                MFX_BIND,
                HASH,
                MFX_BIND_DLC,
                new byte[]{
                        (byte) ((uid >> 24) & 0xFF),
                        (byte) ((uid >> 16) & 0xFF),
                        (byte) ((uid >> 8) & 0xFF),
                        (byte) (uid & 0xFF),
                        (byte) ((sid >> 8) & 0xFF),
                        (byte) (sid & 0xFF),
                        (byte) 0x00,
                        (byte) 0x00
                }
        );
    }

    public static CANPacket s88QueryStatus(final int contactId) {
        return new CANPacket(
                PRIORITY,
                S88_EVENT,
                HASH,
                S88_EVENT_QUERY_DLC,
                new byte[]{
                        (byte) ((contactId >> 24) & 0xFF),
                        (byte) ((contactId >> 16) & 0xFF),
                        (byte) ((contactId >> 8) & 0xFF),
                        (byte) (contactId & 0xFF),
                        (byte) 0,
                        (byte) 0,
                        (byte) 0,
                        (byte) 0
                }
        );
    }

    public static CANPacket requestConfigData(final String fileName) {
        if (fileName.length() > 8) {
            throw new IllegalArgumentException("max fileName length is 8. Length: " + fileName.length());
        }

        byte[] fileNameBytes = fileName.getBytes();
        byte[] dataBytes = new byte[8];

        System.arraycopy(fileNameBytes, 0, dataBytes, 0, fileNameBytes.length); //copy fileNameBytes to dataBytes array

        return new CANPacket(
                PRIORITY,
                REQUEST_CONFIG_DATA,
                HASH,
                REQUEST_CONFIG_DATA_DLC,
                dataBytes
        );
    }

    /**
     * Reads values out of a loc decoder (see page 33 for further information)
     *
     * @param locId
     * @param cvIndex
     * @param cvNumber
     * @param count
     * @return
     */
    public static CANPacket readLocConfig(final int locId, final int cvIndex, final int cvNumber, final int count) {
        if (cvIndex < 0 || cvIndex > 63) {
            throw new IllegalArgumentException("cvIndex must be between 0 and 63 (Error: " + cvIndex + ").");
        }

        if (cvNumber < 1 || cvNumber > 1024) {
            throw new IllegalArgumentException("cvNumber must be between 1 and 1024 (Error: " + cvNumber + ").");
        }

        if (count < 0 || count > 255) {
            throw new IllegalArgumentException("count must be between 0 and 255.");
        }

        //set upper 6 bits of d-byte 4 (note: cvIndex has max. 6 bits set) (by shifting the bits two to the left, the lower two bits are set to zero; these two bits are needed for cvNumber)
        byte byte4 = (byte) (cvIndex << 2);
        //now set the lower 2 bits of d-byte 4 (bits 9 and 10 of cvNumber (max. 10 bits set) are shifted 8 to the right (to position 0 and 1))
        byte4 = (byte) (byte4 | (cvNumber >>> 8)); //or (byte)(byte4 | (cvNumberValue & 0x0000FF00))

        byte byte5 = (byte) (cvNumber & 0xFF);

        return new CANPacket(
                PRIORITY,
                READ_CONFIG,
                HASH,
                READ_CONFIG_DLC,
                new byte[]{
                        (byte) ((locId >> 24) & 0xFF),
                        (byte) ((locId >> 16) & 0xFF),
                        (byte) ((locId >> 8) & 0xFF),
                        (byte) (locId & 0xFF),
                        byte4,
                        byte5,
                        (byte) (count & 0xFF),
                        (byte) 0x00
                }
        );
    }
}
