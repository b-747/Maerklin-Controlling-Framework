package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.*;
import de.cortex42.maerklin.testgui.DebugOutput;

import java.io.IOException;
import java.net.SocketException;

/**
 * Created by ivo on 13.11.15.
 */
public class Main {
    public static void main(String[] args) {
        final int PC_PORT = 15730;
        final int CS2_PORT = 15731;
        final String CS2_IP_ADDRESS = "192.168.16.2";
        final byte[] S88 = new byte[]{0x00, 0x11}; //S88-Link 17
        final byte[] AE_LOK = new byte[]{0x00, 0x00, 0x40, 0x07};
        //final byte[] KONTAKT4 = new byte[]{0x00, 0x04};
        //final byte[] KONTAKT3 = new byte[]{0x00, 0x03};
        final byte[] WEICHE4 = new byte[]{0x00, 0x00, 0x30, 0x03};
        final byte[] WEICHE6 = new byte[]{0x00, 0x00, 0x30, 0x05};
        final byte[] STROMSCHALTER_33 = new byte[]{0x00, 0x00, 0x30, 0x20};
        final byte[] STROMSCHALTER_34 = new byte[]{0x00, 0x00, 0x30, 0x21};

        final PacketListener debugPacketListener = packetEvent -> {
            CANPacket canPacket = packetEvent.getCANPacket();

            DebugOutput.write(String.format("----Received: %s\n\t%s",
                    canPacket.getString(),
                    CANPacketInterpreter.interpretCANPacket(canPacket)));
        };

        final PacketListener contactReachedListener = packetEvent -> {
            CANPacket canPacket = packetEvent.getCANPacket();

            if (canPacket.getCommand() == CS2CANCommands.S88_EVENT + 1
                    && canPacket.getDlc() == CS2CANCommands.S88_EVENT_RESPONSE_DLC) {

                EthernetInterface ethernetInterface = null;

                try {
                    ethernetInterface = EthernetInterface.getInstance(PC_PORT);
                } catch (SocketException e) {
                    e.printStackTrace();
                }

                byte[] data = canPacket.getData();
                //byte[] contact = new byte[]{data[2], data[3]};
                int contact = ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
                byte oldPosition = data[4];
                byte newPosition = data[5];

                switch (contact) {
                    case 3:
                        DebugOutput.write(String.format("contact 3: old position %02X, new position %02X", oldPosition, newPosition));
                        try {
                            ethernetInterface.writeCANPacket(CS2CANCommands.toggleEquipment(WEICHE4, CS2CANCommands.EQUIPMENT_POSITION_ON, CS2CANCommands.EQUIPMENT_POWER_ON), CS2_IP_ADDRESS, CS2_PORT);

                            Thread.sleep(200L);

                            ethernetInterface.writeCANPacket(CS2CANCommands.toggleEquipment(WEICHE4, CS2CANCommands.EQUIPMENT_POSITION_ON, CS2CANCommands.EQUIPMENT_POWER_OFF), CS2_IP_ADDRESS, CS2_PORT);

                            Thread.sleep(200L);

                            ethernetInterface.writeCANPacket(CS2CANCommands.toggleEquipment(WEICHE6, CS2CANCommands.EQUIPMENT_POSITION_ON, CS2CANCommands.EQUIPMENT_POWER_ON), CS2_IP_ADDRESS, CS2_PORT);

                            Thread.sleep(200L);

                            ethernetInterface.writeCANPacket(CS2CANCommands.toggleEquipment(WEICHE6, CS2CANCommands.EQUIPMENT_POSITION_ON, CS2CANCommands.EQUIPMENT_POWER_OFF), CS2_IP_ADDRESS, CS2_PORT);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        break;
                    case 4:
                        DebugOutput.write(String.format("contact 4: old position %02X, new position %02X", oldPosition, newPosition));
                        try {
                            ethernetInterface.writeCANPacket(CS2CANCommands.toggleEquipment(WEICHE4, CS2CANCommands.EQUIPMENT_POSITION_OFF, CS2CANCommands.EQUIPMENT_POWER_ON), CS2_IP_ADDRESS, CS2_PORT);

                            Thread.sleep(200L);

                            ethernetInterface.writeCANPacket(CS2CANCommands.toggleEquipment(WEICHE4, CS2CANCommands.EQUIPMENT_POSITION_OFF, CS2CANCommands.EQUIPMENT_POWER_OFF), CS2_IP_ADDRESS, CS2_PORT);

                            Thread.sleep(200L);

                            ethernetInterface.writeCANPacket(CS2CANCommands.toggleEquipment(WEICHE6, CS2CANCommands.EQUIPMENT_POSITION_OFF, CS2CANCommands.EQUIPMENT_POWER_ON), CS2_IP_ADDRESS, CS2_PORT);

                            Thread.sleep(200L);

                            ethernetInterface.writeCANPacket(CS2CANCommands.toggleEquipment(WEICHE6, CS2CANCommands.EQUIPMENT_POSITION_OFF, CS2CANCommands.EQUIPMENT_POWER_OFF), CS2_IP_ADDRESS, CS2_PORT);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        DebugOutput.write("UNKNOWN CONTACT: " + contact);
                        break;
                }

            }
        };

        EthernetInterface ethernetInterface = null;

        try {
            ethernetInterface = EthernetInterface.getInstance(PC_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        ethernetInterface.addPacketListener(debugPacketListener);
        //ethernetInterface.addPacketListener(contactListener);

       /* try {
            ethernetInterface.writeCANPacket(CS2CANCommands.setVelocity(AE_LOK, 730), CS2_IP_ADDRESS, CS2_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        while (true) ;
        //ethernetInterface.cleanUp();
    }
}
