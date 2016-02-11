package bachelorarbeit.framework;

/**
 * Created by ivo on 19.10.15.
 */
public final class CS2CANCommands {
    /**
     * BIG ENDIAN
     */
    /*---PRIORITY---*/
    public static final byte PRIORITY = 0x00;
    /*---HASH---*/
    public static final byte[] HASH = new byte[]{0x03, 0x00};
    /*---COMMANDS---*/
    public static final byte BOOTLOADER_GO = 0x36;
    public static final byte DIRECTION = 0x0A;
    public static final byte FUNCTION = 0x0C;
    public static final byte SYSTEM = 0x00;
    public static final byte VELOCITY = 0x08;
    public static final byte EQUIPMENT = 0x16;
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
    public static final byte DIRECTION_QUERY_DLC = 0x04;
    public static final byte DIRECTION_SET_DLC = 0x05;
    public static final byte FUNCTION_SET_DLC = 0x06;
    public static final byte SYSTEM_STOP_AND_GO_DLC = 0x05;
    public static final byte SYSTEM_RAIL_UNLOCK_DLC = 0x06;
    public static final byte SYSTEM_MFX_REGISTRATION_COUNTER_DLC = 0x07;
    public static final byte VELOCITY_QUERY_DLC = 0x04;
    public static final byte VELOCITY_SET_DLC = 0x06;
    public static final byte EQUIPMENT_DLC = 0x06;
    public static final byte S88_EVENT_QUERY_DLC = 0x04;
    public static final byte S88_EVENT_RESPONSE_DLC = 0x08;
    public static final byte REQUEST_CONFIG_DATA_DLC = 0x08;
    public static final byte GET_CONFIG_DATA_STREAM_REQUEST_RESPONSE_DLC = 0x06;
    public static final byte GET_CONFIG_DATA_STREAM_FIRST_PACKET_CONFIG_CHANGED_DLC = 0x07;
    public static final byte GET_CONFIG_DATA_STREAM_PACKET_DLC = 0x08;
    /*---SYSTEM_RAIL_UNLOCK_PARAM---*/
    public static final byte SYSTEM_RAIL_UNLOCK_PARAM = 0b00000111; //see protocol 2.9 (unlocks mm2, mfx, dcc)
    /*---SYSTEM_MFX_REGISTRATION_COUNTER_VALUE---*/
    public static final byte[] SYSTEM_MFX_REGISTRATION_COUNTER_VALUE = new byte[]{0x00, 0x02};
    /**
     * Command flags.
     */
    /*---DIRECTION---*/
    public static final byte DIRECTION_MAINTAIN = 0x00;
    public static final byte DIRECTION_FORWARD = 0x01;
    public static final byte DIRECTION_BACKWARD = 0x02;
    public static final byte DIRECTION_TOGGLE = 0x03;
    /*---FUNCTION---*/
    public static final byte FUNCTION_OFF = 0x00;
    public static final byte FUNCTION_ON = 0x01;
    /*---CONTACT_STATE---*/
    public static final byte CONTACT_DEACTIVATED = 0x00;
    public static final byte CONTACT_ACTIVATED = 0x01;
    /*---EQUIPMENT POWER---*/
    public static final byte EQUIPMENT_POWER_OFF = 0x00;
    public static final byte EQUIPMENT_POWER_ON = 0x01;
    /*---SWITCH POSITION---*/
    public static final byte SWITCH_POSITION_ROUND = 0x00;
    public static final byte SWITCH_POSITION_STRAIGHT = 0x01;

    private CS2CANCommands() {
        /* ... */
    }

    //wait 400ms after sending bootloader go!
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
     * @param locId
     * @param position    1 = straight
     * @param powerToggle
     * @return
     */
    public static CANPacket toggleEquipment(final int locId, final int position, final int powerToggle) { //todo doku, parameter
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

    public static CANPacket requestConfigData(final String fileName) {
        if (fileName.length() > 8) {
            throw new IllegalArgumentException("max fileName length is 8. Length: " + fileName.length());
        }

        final byte[] fileNameBytes = fileName.getBytes();
        final byte[] dataBytes = new byte[8];

        System.arraycopy(fileNameBytes, 0, dataBytes, 0, fileNameBytes.length); //copy fileNameBytes to dataBytes array

        return new CANPacket(
                PRIORITY,
                REQUEST_CONFIG_DATA,
                HASH,
                REQUEST_CONFIG_DATA_DLC,
                dataBytes
        );
    }
}
