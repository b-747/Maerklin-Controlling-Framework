package de.cortex42.maerklin.framework;

/**
 * Created by ivo on 19.10.15.
 */
public final class CS2CANCommands {
    /*---PRIORITY---*/
    public static final byte PRIORITY = 0x00;
    /*---HASH---*/
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
    public static final byte DISCOVERY = 0x02; //only master should send this! (if CS2 available, then read locs from .cs2 file)
    public static final byte MFX_BIND = 0x04; //only master should send this!
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
    public static final byte DISCOVERY_POSITIVE_ANSWER_DLC = 0x05;     //answers to previous dlc (0x01)
    public static final byte DISCOVERY_NEGATIVE_ANSWER_DLC = 0x00;
    public static final byte MFX_BIND_DLC = 0x06;
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
    /*---EQUIPMENT---*/
    public static final byte EQUIPMENT_OFF = 0x00;
    public static final byte EQUIPMENT_ON = 0x01;
    /*---EQUIPMENT_POSITION---*/
    public static final byte POSITION_OFF = 0x00;
    public static final byte POSITION_ON = 0x01;

    private CS2CANCommands(){
        /* ... */
    }

    public static CANPacket bootloaderGo(){
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

    public static CANPacket ping(){
        return new CANPacket(
                PRIORITY,
                PING,
                HASH,
                PING_QUERY_DLC, //query bus participants
                new byte[8] //all 0x00
        );
    }

    public static CANPacket queryDirection(){
        return new CANPacket(
                PRIORITY,
                DIRECTION,
                HASH,
                DIRECTION_QUERY_DLC,
                new byte[8] //all 0x00
        );
    }

    public static CANPacket setDirection(byte[] locId, byte direction){
        if(locId.length != 4){
            throw new IllegalArgumentException("locId must be 4 Bytes instead of "+locId.length);
        }

        if(direction > 3 || direction < 0){
            throw new IllegalArgumentException("direction must be between 0 and 3. (Error: "+ direction +").");
        }

        return new CANPacket(
                PRIORITY,
                DIRECTION,
                HASH,
                DIRECTION_SET_DLC,
                new byte[]{
                        locId[0],
                        locId[1],
                        locId[2],
                        locId[3],
                        direction,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00
                }
        );
    }

    public static CANPacket queryFunction(byte function, byte[] locId){
        if(function > 31){
            throw new IllegalArgumentException("function must be between 0 and 31. (Error: "+function+").");
        }

        return new CANPacket(
                PRIORITY,
                FUNCTION,
                HASH,
                FUNCTION_QUERY_DLC,
                new byte[]{
                        locId[0],
                        locId[1],
                        locId[2],
                        locId[3],
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00
                }
        );
    }

    public static CANPacket toggleFunction(byte function, byte[] locId, byte toggle){
        if(locId.length != 4){
            throw new IllegalArgumentException("locId must be 4 Bytes instead of "+locId.length);
        }

        if(function > 31 || function < 0){
            throw new IllegalArgumentException("function must be between 0 and 31. (Error: "+function+").");
        }

        if(toggle > 1 || toggle < 0){
            throw new IllegalArgumentException("toggle is either 0 or 1. (Error: "+toggle+").");
        }

        return new CANPacket(
                PRIORITY,
                FUNCTION,
                HASH,
                FUNCTION_SET_DLC,
                new byte[]{
                        locId[0],
                        locId[1],
                        locId[2],
                        locId[3],
                        function,
                        toggle,
                        (byte)0x00,
                        (byte)0x00
                }
        );
    }

    public static CANPacket stop(){
        return new CANPacket(
                PRIORITY,
                SYSTEM,
                HASH,
                SYSTEM_STOP_AND_GO_DLC,
                new byte[]{
                        (byte)0x00, //target all (UID = 0x00000000)
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        SYSTEM_STOP_SUBCMD,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00
                }
        );
    }

    public static CANPacket go(){
        return new CANPacket(
                PRIORITY,
                SYSTEM,
                HASH,
                SYSTEM_STOP_AND_GO_DLC,
                new byte[]{
                        (byte)0x00, //target all (UID = 0x00000000)
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        SYSTEM_GO_SUBCMD,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00
                }
        );
    }

    public static CANPacket unlockRail(){
        return new CANPacket(
                PRIORITY,
                SYSTEM,
                HASH,
                SYSTEM_RAIL_UNLOCK_DLC,
                new byte[]{
                        (byte)0x00, //target all (UID = 0x00000000)
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        SYSTEM_RAIL_UNLOCK_SUBCMD,
                        SYSTEM_RAIL_UNLOCK_PARAM, //unlock mfx, mm2, dcc
                        (byte)0x00,
                        (byte)0x00
                }
        );
    }

    public static CANPacket newRegistration(){
        return new CANPacket(
                PRIORITY,
                SYSTEM,
                HASH,
                SYSTEM_MFX_REGISTRATION_COUNTER_DLC,
                new byte[]{
                        (byte)0x00, //target all (UID = 0x00000000)
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        SYSTEM_MFX_REGISTRATION_COUNTER_SUBCMD,
                        SYSTEM_MFX_REGISTRATION_COUNTER_VALUE[0],
                        SYSTEM_MFX_REGISTRATION_COUNTER_VALUE[1],
                        (byte)0x00
                }
        );
    }

    public static CANPacket queryVelocity(byte[] locId){
        if(locId.length != 4){
            throw new IllegalArgumentException("locId must be 4 Bytes instead of "+locId.length);
        }

        return new CANPacket(
                PRIORITY,
                VELOCITY,
                HASH,
                VELOCITY_QUERY_DLC,
                new byte[]{
                        locId[0],
                        locId[1],
                        locId[2],
                        locId[3],
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00
                }
        );
    }

    public static CANPacket setVelocity(byte[] locId, int velocity){
        if(locId.length != 4){
            throw new IllegalArgumentException("locId must be 4 Bytes instead of "+locId.length);
        }

        return new CANPacket(
                PRIORITY,
                VELOCITY,
                HASH,
                VELOCITY_SET_DLC,
                new byte[]{
                        locId[0],
                        locId[1],
                        locId[2],
                        locId[3],
                        //max velocity = 0x03E8 = 1000
                        (byte)((velocity & 0x0000FF00) >> 8),
                        (byte)(velocity & 0x000000FF),
                        (byte)0x00,
                        (byte)0x00
                }
        );
    }

    public static CANPacket toggleEquipment(byte[] id, byte position, byte toggle){
        if(id.length != 4){
            throw new IllegalArgumentException("id must be 4 Bytes instead of "+id.length);
        }

        return new CANPacket(
                PRIORITY,
                EQUIPMENT,
                HASH,
                EQUIPMENT_DLC,
                new byte[]{
                        id[0],
                        id[1],
                        id[2],
                        id[3],
                        position,
                        toggle,
                        (byte)0x00,
                        (byte)0x00
                }
        );
    }

    /**
     * Discovery of every loc with every protocol.
     * Should only be sent by the master!
     * @return
     */
    public static CANPacket discoverAllLocs(){
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
     * @param protocol protocol id (see page 26 of the CS2 protocol)
     * @return
     */
    public static CANPacket discoverSpecificLocs(byte protocol){
        if(protocol < 0 || protocol > 96){
            throw new IllegalArgumentException("protocol must be between 0 and 96.");
        }

        return new CANPacket(
                PRIORITY,
                DISCOVERY,
                HASH,
                DISCOVERY_DISCOVER_PROTOCOL_DLC,
                new byte[]{
                        protocol,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00,
                        (byte)0x00
                }
        );
    }

    /**
     * Assigns a sid to a mfx decoder.
     * Should only be sent by the master!
     * @param uid
     * @param sid
     * @return
     */
    public static CANPacket mfxBind(byte[] uid, byte[] sid){
        if(uid.length != 4){
            throw new IllegalArgumentException("uid must be 4 Bytes.");
        }

        if(sid.length != 2){
            throw new IllegalArgumentException("sid must be 2 Bytes.");
        }

        return new CANPacket(
                PRIORITY,
                MFX_BIND,
                HASH,
                MFX_BIND_DLC,
                new byte[]{
                        uid[0],
                        uid[1],
                        uid[2],
                        uid[3],
                        sid[0],
                        sid[1],
                        (byte)0x00,
                        (byte)0x00
                }
        );
    }
}
