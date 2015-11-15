package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.*;
import de.cortex42.maerklin.testgui.DebugOutput;

import java.net.SocketException;

/**
 * Created by ivo on 13.11.15.
 */
public class Main {
    public static void main(String[] args) {
        final int PC_PORT = 15730;
        final int CS2_PORT = 15731;
        final String CS2_IP_ADDRESS = "192.168.16.2";

        final int S88 = 17; //S88-Link 17
        final int MFX_RANGE = 16384; //new byte[]{0x00, 0x00, 0x40, 0x00};
        final int AE_LOK = 7; //new byte[]{0x00, 0x00, 0x40, 0x07};

        final int MM2_EQUIPMENT_RANGE = 12288; //= new byte[]{0x00, 0x00, 0x30, 0x00};
        final int WEICHE4 = 3; //new byte[]{0x00, 0x00, 0x30, 0x03};
        final int WEICHE6 = 5; //new byte[]{0x00, 0x00, 0x30, 0x05};
        final int STROMSCHALTER_33 = 32; //new byte[]{0x00, 0x00, 0x30, 0x20};
        final int STROMSCHALTER_34 = 33; //new byte[]{0x00, 0x00, 0x30, 0x21};

        final PacketListener debugPacketListener = new PacketListener() {
            @Override
            public void packetEvent(PacketEvent packetEvent) {
                CANPacket canPacket = packetEvent.getCANPacket();

                DebugOutput.write(String.format("----Received: %s\n\t%s",
                        canPacket.getString(),
                        CANPacketInterpreter.interpretCANPacket(canPacket)));
            }
        };

        EthernetInterface ethernetInterface = null;

        try {
            ethernetInterface = EthernetInterface.getInstance(PC_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        ethernetInterface.addPacketListener(debugPacketListener);

        while (true) ;
        //ethernetInterface.cleanUp();
    }
}
