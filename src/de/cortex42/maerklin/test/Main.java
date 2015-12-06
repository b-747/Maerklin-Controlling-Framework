package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.CANPacket;
import de.cortex42.maerklin.framework.CANPacketInterpreter;
import de.cortex42.maerklin.framework.PacketEvent;
import de.cortex42.maerklin.framework.PacketListener;
import de.cortex42.maerklin.testgui.DebugOutput;

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

        final String configDataStreamCompressedAsString = "0000071E789CCD944D6E83301085F739051748EA7FE8C28BF608D9565DD0C4A828015740687AFB8E21B849FA22214595BAB2FD3CF366C67CE265EF77BEF25DD9BBD745EF9AB6F4F522595565ED1B2B17AD6B4F4AB9A5E34F3029755E39CB339DB0942D1909078A6147C598A643BE6D28D791104EDDD787AD8A23EDDA212868741E33D24D5148ED1E43958DAFEDF33A21DBE0D15B1ED6B7D3DAFBFD21D40C86F9E6DD57F9D186C27DD808398450E7568CEEA1684ADBE250EF3A9AC28539567533E40C2D05A7D5A76BBA61F72B0E8A028912892A96C9D0B546A29972142C0EC7C9620EECED118EC6A08A9F01DA7238335750D5009DB54B9461448F59CA737ACC053D06D063AEE9C9B5E3133DA3ED6D7A84D61119CD2664CCBD9C0C71F049C4742BE0B58C4524C4640645A33BFCA233781AB3212533C81AAFFF29624F2E310F26E15C09758E587A81580A104BAF11CB32554C8805DBA59987988A88C1B6EFFE15CD220BBEEEFD60DDC6894BD8112C142912B8128F6F8421FD43A2BE017B2D15D400";
        final String configDataStreamCompressedAsString2 = "789CCD944D6E83301085F739051748EA7FE8C28BF608D9565DD0C4A828015740687AFB8E21B849FA22214595BAB2FD3CF366C67CE265EF77BEF25DD9BBD745EF9AB6F4F522595565ED1B2B17AD6B4F4AB9A5E34F3029755E39CB339DB0942D1909078A6147C598A643BE6D28D791104EDDD787AD8A23EDDA212868741E33D24D5148ED1E43958DAFEDF33A21DBE0D15B1ED6B7D3DAFBFD21D40C86F9E6DD57F9D186C27DD808398450E7568CEEA1684ADBE250EF3A9AC28539567533E40C2D05A7D5A76BBA61F72B0E8A028912892A96C9D0B546A29972142C0EC7C9620EECED118EC6A08A9F01DA7238335750D5009DB54B9461448F59CA737ACC053D06D063AEE9C9B5E3133DA3ED6D7A84D61119CD2664CCBD9C0C71F049C4742BE0B58C4524C4640645A33BFCA233781AB3212533C81AAFFF29624F2E310F26E15C09758E587A81580A104BAF11CB32554C8805DBA59987988A88C1B6EFFE15CD220BBEEEFD60DDC6894BD8112C142912B8128F6F8421FD43A2BE017B2D15D400";
        final int expectedCrc = 0x2E21; //correct calcCRC

        final PacketListener debugPacketListener = new PacketListener() {
            @Override
            public void packetEvent(PacketEvent packetEvent) {
                CANPacket canPacket = packetEvent.getCANPacket();

                DebugOutput.write(String.format("----Received: %s\n\t%s",
                        canPacket.getString(),
                        CANPacketInterpreter.interpretCANPacket(canPacket)));
            }
        };

      /*  EthernetInterface ethernetInterface = null;


        try {
            ethernetInterface = EthernetInterface.getInstance(PC_PORT);
        } catch (FrameworkException e) {
            e.printStackTrace();
        }

        final EthernetInterface finalEthernetInterface = ethernetInterface;*/

        // ethernetInterface.addPacketListener(debugPacketListener);
        //ethernetInterface.addPacketListener(configDataStreamFirstPacketListener);
       /* try {
            ethernetInterface.writeCANPacket(CS2CANCommands.requestConfigData("loks"), CS2_IP_ADDRESS, CS2_PORT);
        } catch (FrameworkException e) {
            e.printStackTrace();
        }*/

        /*Script script = null;
        try {
            script = TestScripts.getTestScript(new ScriptContext(ethernetInterface, CS2_IP_ADDRESS, CS2_PORT));
        } catch (FrameworkException e) {
            e.printStackTrace();
        }

        try {
            script.execute();
        } catch (FrameworkException e) {
            System.out.println(e.getMessage());
        }*/

       /* try {
            Thread.sleep(20000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //while(true);
        //ethernetInterface.cleanUp();
    }

    /*
    * Hash 0x0300: wird gebildet aus UID 0x00000000 oder 0xFFFFFFFF (sollte also mit real existierender Hardware nicht kollidieren)
    * */
    public static int calcHash(int uid) {
        return (((uid >> 16) ^ (uid & 0xFFFF)) & 0xFF7F) | 0x0300;
        /*  (upper 16 bits XOR lower 16 bits) AND Bit7=0 OR Bit8/9=1   */
    }

    public static void pause() {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
