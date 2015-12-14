package de.cortex42.maerklin.testgui;

import de.cortex42.maerklin.framework.*;

import java.math.BigInteger;

/**
 * Created by ivo on 21.10.15.
 */
public class Presenter {
    private final View view;
    private EthernetConnection ethernetConnection;
    private SerialPortConnection serialPortConnection;
    private Connection connection;
    private boolean useEthernet = false;

    private final static int BAUD = 500000;
    private final static int DATA_BITS = 8;
    private final static int STOP_BITS = 1;
    private final static int PARITY_BIT = 0;

/*
    private static final int MFX_RANGE = 16384; //new byte[]{0x00, 0x00, 0x40, 0x00};
    private static final int MM_RANGE = 0; //new byte[]{0x00, 0x00, 0x00, 0x00};
    private static final int MM2_EQUIPMENT_RANGE = 12288; //new byte[]{0x00, 0x00, 0x30, 0x00};


    private static final int MM_LOC_ID = 78; //new byte[]{0x00, 0x00, 0x00, 0x4E}; //78 = BR 81 (MM)
    private static final int MFX_LOC_ID = 6; //RE 460 076-3 (MFX)
    private static final int MFX_LOC_ID2 = 7; //AE 6/6 11424

      if(Objects.equals(selectedLoc, mmLoc)){
            selectedLocId = MM_RANGE | MM_LOC_ID;
        }else if(Objects.equals(selectedLoc, mfxLoc1)){
            selectedLocId = MFX_RANGE | MFX_LOC_ID;
        }else{
            selectedLocId = MFX_RANGE | MFX_LOC_ID2;
        }
*/


    private static final int LIGHT_FUNCTION = 0;

    private static final int PC_PORT = 15730;
    private static final int CS2_PORT = 15731;

    private static final long DEFAULT_DELAY = 250L; //todo check this delay!

    public Presenter(View view){
        this.view = view;
        serialPortConnection = SerialPortConnection.getInstance();
        this.view.addSerialPorts(serialPortConnection.getAvailableSerialPorts());
    }

    public void useEthernetConnection(boolean use) {
        if (use) {
            try {
                connection = ethernetConnection = new EthernetConnection(PC_PORT, CS2_PORT, view.getIpAddress());
            } catch (FrameworkException e) {
                e.printStackTrace();
            }
        }else{
            serialPortConnection.closePort();
            serialPortConnection.openPort(view.getSerialPort(), BAUD, DATA_BITS, STOP_BITS, PARITY_BIT);
            connection = serialPortConnection;
        }
    }

    public void sendStart(){
        sendPacket(CS2CANCommands.newRegistration());
        pause(DEFAULT_DELAY);
        sendPacket(CS2CANCommands.unlockRail());
        pause(DEFAULT_DELAY);

        sendPacket(CS2CANCommands.go());
    }

    public void sendStop(){
        sendPacket(CS2CANCommands.stop());
    }

    public void sendBootloaderGo(){
        sendPacket(CS2CANCommands.bootloaderGo());
        pause(400L); //pause 400ms after sending bootloader go command
    }

    public void sendLight(boolean on){
        sendPacket(CS2CANCommands.toggleFunction(view.getLoc(), LIGHT_FUNCTION, on ? CS2CANCommands.FUNCTION_ON : CS2CANCommands.FUNCTION_OFF));
    }

    public void sendVelocity(int velocity){
        sendPacket(CS2CANCommands.setVelocity(view.getLoc(), velocity));
    }

    public void sendToggleDirection(){
        int locId = view.getLoc();

        final int[] velocity = new int[1];

        addPacketListener(
                new PacketListener() {
                    @Override
                    public void packetEvent(PacketEvent packetEvent) {
                        byte command = packetEvent.getCANPacket().getCommand();

                        if((command & CS2CANCommands.RESPONSE) == CS2CANCommands.RESPONSE){
                            //toggle reponse bit
                            command = (byte)(command & ~CS2CANCommands.RESPONSE);
                        }

                        if (command != CS2CANCommands.VELOCITY) {
                            return;
                        }

                        byte[] data = packetEvent.getCANPacket().getData();
                        byte[] velocityBytes = new byte[]{data[4], data[5]};
                        velocity[0] = ((velocityBytes[0] & 0xFF) << 8 | (velocityBytes[1] & 0xFF));

                        removePacketListener(this);
                    }
                }
        );

        sendPacket(CS2CANCommands.queryVelocity(locId));
        pause(DEFAULT_DELAY);


        sendPacket(CS2CANCommands.setDirection(locId, CS2CANCommands.DIRECTION_TOGGLE)); //toggle direction
        pause(DEFAULT_DELAY);

        addPacketListener(
                new PacketListener() {
                    @Override
                    public void packetEvent(PacketEvent packetEvent) {
                        byte command = packetEvent.getCANPacket().getCommand();

                        if((command & CS2CANCommands.RESPONSE) == CS2CANCommands.RESPONSE){
                            //toggle reponse bit
                            command = (byte)(command & ~CS2CANCommands.RESPONSE);
                        }

                        if(command != CS2CANCommands.DIRECTION){
                            return;
                        }

                        byte[] data = packetEvent.getCANPacket().getData();
                        byte direction= data[4];

                        switch (direction) {
                            case CS2CANCommands.DIRECTION_FORWARD:
                                view.setDirection("FORWARD");
                                break;

                            case CS2CANCommands.DIRECTION_BACKWARD:
                                view.setDirection("BACKWARD");
                                break;
                        }

                        removePacketListener(this);

                    }
                }
        );
        sendVelocity(velocity[0]);
        pause(DEFAULT_DELAY);

        sendStart();
        pause(DEFAULT_DELAY);

        sendPacket(CS2CANCommands.queryDirection(locId)); //query new direction
    }

    public void cleanUp(){
        if (ethernetConnection != null) {
            ethernetConnection.cleanUp();
        }

        serialPortConnection.closePort();
    }

    private void sendQueryVelocity(){
        addPacketListener(
                new PacketListener() {
                    @Override
                    public void packetEvent(PacketEvent packetEvent) {
                        byte command = packetEvent.getCANPacket().getCommand();

                        if((command & CS2CANCommands.RESPONSE) == CS2CANCommands.RESPONSE){
                            //toggle reponse bit
                            command = (byte)(command & ~CS2CANCommands.RESPONSE);
                        }

                        if (command != CS2CANCommands.VELOCITY
                                && packetEvent.getCANPacket().getDlc() != CS2CANCommands.VELOCITY_SET_DLC) {
                            return;
                        }

                        byte[] data = packetEvent.getCANPacket().getData();
                        byte[] velocityBytes = new byte[]{data[4], data[5]};
                        int velocity = new BigInteger(velocityBytes).intValue();
                        view.setVelocity(velocity);

                        removePacketListener(this);
                    }
                }
        );

        sendPacket(CS2CANCommands.queryVelocity(view.getLoc()));
    }

    public void sendGetLoks() {
        addPacketListener(new ConfigDataStreamPacketListener() {
            @Override
            public void bytesDecompressed(final byte[] decompressedBytes) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < decompressedBytes.length; i++) {
                    stringBuilder.append((char) decompressedBytes[i]);
                }

                view.showConfigData(stringBuilder.toString());

                //todo add errorhandler

                removePacketListener(this);
            }
        });

        sendPacket(CS2CANCommands.requestConfigData("loks"));
    }

    private void sendPacket(CANPacket canPacket) {
        try {
            connection.writeCANPacket(canPacket);
        } catch (FrameworkException e) {
            e.printStackTrace();
        }
    }

    private void addPacketListener(PacketListener packetListener){
        connection.addPacketListener(packetListener);
    }

    private void removePacketListener(PacketListener packetListener){
        connection.removePacketListener(packetListener);
    }

    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
