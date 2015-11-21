package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.*;
import de.cortex42.maerklin.testgui.DebugOutput;

import java.util.zip.DataFormatException;

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

        final PacketListener debugPacketListener = new PacketListener() {
            @Override
            public void packetEvent(PacketEvent packetEvent) {
                CANPacket canPacket = packetEvent.getCANPacket();

                DebugOutput.write(String.format("----Received: %s\n\t%s",
                        canPacket.getString(),
                        CANPacketInterpreter.interpretCANPacket(canPacket)));
            }
        };

        //final PacketListener[] configDataStreamPacketListener = new PacketListener[1];

        /*final PacketListener configDataStreamFirstPacketListener = new PacketListener() {
            @Override
            public void packetEvent(PacketEvent packetEvent) {
                CANPacket canPacket = packetEvent.getCANPacket();

                if (canPacket.getCommand() == CS2CANCommands.GET_CONFIG_DATA_STREAM
                        && canPacket.getDlc() == CS2CANCommands.GET_CONFIG_DATA_STREAM_FIRST_PACKET_REQUEST_RESPONSE_DLC) {
                    //received the first packet in the stream

                    //now get the file/stream length in bytes and the crc of the bytes
                    byte[] data = canPacket.getData();
                    byte[] fileLength = new byte[]{data[0], data[1], data[2], data[3]};
                    int fileLengthInBytes =
                            ((fileLength[0] & 0xFF) << 24) | ((fileLength[1] & 0xFF) << 16)
                            | ((fileLength[2] & 0xFF) << 8) | ((fileLength[3] & 0xFF) *//*<<0*//*);

                    byte[] crc = new byte[]{data[4], data[5]};

                    //first packet received, now create another listener for the following data packets
                    configDataStreamPacketListener[0] = new PacketListener() {
                        int bytesReceived = 0;
                        byte[] data = new byte[fileLengthInBytes];

                        @Override
                        public void packetEvent(PacketEvent packetEvent) {
                            CANPacket canPacket = packetEvent.getCANPacket();

                            if (canPacket.getCommand() == CS2CANCommands.GET_CONFIG_DATA_STREAM
                                    && canPacket.getDlc() == CS2CANCommands.GET_CONFIG_DATA_STREAM_PACKET_DLC) {

                                System.arraycopy(canPacket.getData(), 0, data, bytesReceived, canPacket.getData().length);
                                bytesReceived += canPacket.getData().length;


                                if(bytesReceived == fileLengthInBytes){
                                    //todo remove listener
                                    int calculatedCRC = UncompressConfigData.calcCRC(data);
                                    int expectedCRC = (((crc[0] & 0xFF) << 8) | (crc[1] & 0xFF));

                                    if(calculatedCRC == expectedCRC){
                                        DebugOutput.write(String.format("Correct crc %d",calculatedCRC));
                                    }else{
                                        DebugOutput.write(String.format("Expected crc %d, actual crc %d", expectedCRC, calculatedCRC));
                                    }

                                    //inflate data
                                    try {
                                        byte[] uncompressed = UncompressConfigData.uncompressBytes(data);

                                        for(byte b : uncompressed){
                                            System.out.print((char)(b & 0xFF));
                                        }
                                    } catch (DataFormatException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    };
                }
            }
        };*/

/*
        EthernetInterface ethernetInterface = null;

        try {
            ethernetInterface = EthernetInterface.getInstance(PC_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }*/

        SerialPortInterface serialPortInterface = SerialPortInterface.getInstance();
        serialPortInterface.openPort(serialPortInterface.getAvailableSerialPorts().get(0));
        serialPortInterface.addPacketListener(debugPacketListener);

        serialPortInterface.writeCANPacket(CS2CANCommands.bootloaderGo());
        pause();
        serialPortInterface.writeCANPacket(CS2CANCommands.setVelocity(78, 0));
        pause();
        serialPortInterface.writeCANPacket(CS2CANCommands.newRegistration());
        pause();
        serialPortInterface.writeCANPacket(CS2CANCommands.unlockRail());
        pause();
        serialPortInterface.writeCANPacket(CS2CANCommands.go());

        //Script script = TestScripts.getTestScript(new ScriptContext(ethernetInterface, CS2_IP_ADDRESS, CS2_PORT));
        Script script = TestScripts.getLittleTestScript(new ScriptContext(serialPortInterface));
        (new Thread(new Runnable() {
            @Override
            public void run() {
                while (TestScripts.waitingTime1 <= 20000L) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    TestScripts.waitingTime1 += 1000L;
                }
            }
        })).start();

        script.execute();

        while (true) ;
    }

    public static void uncompressTest(byte[] data, int expectedCRC) {
        int calculatedCRC = UncompressConfigData.calcCRC(data);
        //int expectedCRC = (((crc[0] & 0xFF) << 8) | (crc[1] & 0xFF));

        if (calculatedCRC == expectedCRC) {
            DebugOutput.write(String.format("Correct crc %d", calculatedCRC));
        } else {
            DebugOutput.write(String.format("Expected crc %d, actual crc %d", expectedCRC, calculatedCRC));
        }

        //inflate data
        try {
            byte[] uncompressed = UncompressConfigData.uncompressBytes(data);

            for (byte b : uncompressed) {
                System.out.print((char) (b & 0xFF));
            }
        } catch (DataFormatException e) {
            e.printStackTrace();
        }
    }

    public static void pause() {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
