package de.cortex42.maerklin.framework;

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;

/**
 * Created by ivo on 26.10.15.
 */
public final class CANPacketInterpreter {
    private CANPacketInterpreter(){
    }

    public static String interpretCANPacket(CANPacket canPacket){
        byte command = canPacket.getCommand();
        final byte dlc = canPacket.getDlc();
        final byte[] data = canPacket.getData();
        boolean response = false;

        StringBuilder stringBuilder = new StringBuilder();

        //check if packet is a response
        if((command & CS2CANCommands.RESPONSE) == CS2CANCommands.RESPONSE){
            response = true;
            stringBuilder.append("RESPONSE ");
            //toggle reponse bit
            command = (byte)(command & ~CS2CANCommands.RESPONSE);
        }

        switch (command){

            case CS2CANCommands.BOOTLOADER_GO:
                stringBuilder.append("BOOTLOADER_GO ");
                break;

            case CS2CANCommands.PING:
                stringBuilder.append("PING ");

                switch(dlc) {
                    case CS2CANCommands.PING_QUERY_DLC:
                        stringBuilder.append("QUERY ");
                        break;

                    case CS2CANCommands.PING_RESPONSE_DLC:
                        stringBuilder.append("RESPONSE ");
                        stringBuilder.append(String.format(
                                "SENDER UID %d, SW VERSION %s, DEVICE ID %s",
                                canPacket.getID(),
                                DatatypeConverter.printHexBinary(new byte[]{data[4], data[5]}),
                                DatatypeConverter.printHexBinary(new byte[]{data[6], data[7]}))
                        );
                        break;

                    default:
                        stringBuilder.append(String.format("UNKNOWN DLC %02X", dlc));
                        break;
                }

                break;

            case CS2CANCommands.DIRECTION:
                stringBuilder.append("DIRECTION ");

                if(dlc == CS2CANCommands.DIRECTION_QUERY_DLC){
                    stringBuilder.append("QUERY ");
                    stringBuilder.append(String.format("LOC ID %d ",
                            canPacket.getID())
                    );
                }else if(dlc == CS2CANCommands.DIRECTION_SET_DLC){
                    stringBuilder.append(String.format("LOC ID %d ",
                            canPacket.getID())
                    );

                    stringBuilder.append("DIRECTION ");
                    switch(data[4]){
                        case CS2CANCommands.DIRECTION_MAINTAIN:
                            stringBuilder.append("MAINTAIN ");
                            break;
                        case CS2CANCommands.DIRECTION_FORWARD:
                            stringBuilder.append("FORWARD ");
                            break;
                        case CS2CANCommands.DIRECTION_BACKWARD:
                            stringBuilder.append("BACKWARD ");
                            break;
                        case CS2CANCommands.DIRECTION_TOGGLE:
                            stringBuilder.append("TOGGLE ");
                            break;
                        default:
                            stringBuilder.append("REMAIN ");
                            break;
                    }
                }else{
                    stringBuilder.append(String.format("UNKNOWN DLC %02X ", dlc));
                }

                break;

            case CS2CANCommands.FUNCTION:
                stringBuilder.append("FUNCTION ");

                switch(dlc) {
                    case CS2CANCommands.FUNCTION_QUERY_DLC:
                        stringBuilder.append("QUERY ");
                        stringBuilder.append(String.format("FUNCTION %02X ", data[4]));

                        if (response) {
                            if (data[5] == CS2CANCommands.FUNCTION_OFF) {
                                stringBuilder.append("OFF ");
                            } else if (data[5] == CS2CANCommands.FUNCTION_ON) {
                                stringBuilder.append("ON ");
                            } else {
                                stringBuilder.append(String.format("UNKNOWN FUNCTION VALUE %02X ", data[5]));
                            }
                        }
                        break;

                    case CS2CANCommands.FUNCTION_SET_DLC:
                        stringBuilder.append("SET ");
                        stringBuilder.append(String.format("FUNCTION %02X ", data[4]));

                        if (data[5] == CS2CANCommands.FUNCTION_OFF) {
                            stringBuilder.append("OFF ");
                        } else if (data[5] == CS2CANCommands.FUNCTION_ON) {
                            stringBuilder.append("ON ");
                        } else {
                            stringBuilder.append(String.format("UNKNOWN FUNCTION VALUE %02X ", data[5]));
                        }
                        break;

                    default:
                        stringBuilder.append(String.format("UNKNOWN DLC %02X ", dlc));
                        break;
                }

                break;

            case CS2CANCommands.SYSTEM:
                stringBuilder.append("SYSTEM ");

                switch(data[4]) { //SUBCMD

                    case CS2CANCommands.SYSTEM_STOP_SUBCMD:
                        stringBuilder.append("STOP ");
                        stringBuilder.append(String.format("UID %d ",
                                canPacket.getID())
                        );

                        break;

                    case CS2CANCommands.SYSTEM_GO_SUBCMD:
                        stringBuilder.append("GO ");
                        stringBuilder.append(String.format("UID %d ",
                                canPacket.getID())
                        );

                        break;

                    case CS2CANCommands.SYSTEM_RAIL_UNLOCK_SUBCMD:
                        stringBuilder.append("RAIL UNLOCK ");
                        stringBuilder.append(String.format("PARAM %02X ", data[5]));

                        break;

                    case CS2CANCommands.SYSTEM_MFX_REGISTRATION_COUNTER_SUBCMD:
                        stringBuilder.append("MFX REGISTRATION ");
                        stringBuilder.append(String.format("COUNTER VALUE %s ",
                                        DatatypeConverter.printHexBinary(new byte[]{data[5], data[6]}))
                        );

                        break;

                    default:
                        stringBuilder.append(String.format("UNKNOWN SUB CMD %02X ", data[4]));

                        break;
                }

                break;

            case CS2CANCommands.VELOCITY:
                stringBuilder.append("VELOCITY ");

                switch(dlc){

                    case CS2CANCommands.VELOCITY_QUERY_DLC:
                        stringBuilder.append("QUERY ");
                        stringBuilder.append(String.format("LOC ID %d ",
                                canPacket.getID())
                        );
                        break;

                    case CS2CANCommands.VELOCITY_SET_DLC:
                        if(response) {
                            stringBuilder.append("RESPONSE ");
                        }else {
                            stringBuilder.append("SET ");
                        }

                        stringBuilder.append(String.format("LOC ID %d ",
                               canPacket.getID())
                        );

                        byte[] velocity = new byte[]{data[4], data[5]};
                        stringBuilder.append(String.format("VELOCITY %s (%d) ",
                                DatatypeConverter.printHexBinary(velocity),
                                new BigInteger(velocity).intValue())
                        );

                        break;

                    default:
                        stringBuilder.append(String.format("UNKNOWN DLC %02X ", dlc));
                        break;
                }

                break;

            default:
                stringBuilder.append(
                        String.format("UNKNOWN COMMAND %02X ",command)
                );
                break;
        }

        return stringBuilder.append("\n").toString();
    }
}
