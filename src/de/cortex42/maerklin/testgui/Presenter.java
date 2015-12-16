package de.cortex42.maerklin.testgui;

import de.cortex42.maerklin.framework.*;
import de.cortex42.maerklin.framework.packetlistener.ConfigDataStreamPacketListener;
import de.cortex42.maerklin.framework.packetlistener.PacketEvent;
import de.cortex42.maerklin.framework.packetlistener.PacketListener;

/**
 * Created by ivo on 21.10.15.
 */
public class Presenter {
    private final View view;
    private Connection connection;
    private int loc;
    private String ipAddress;
    private String serialPort;
    private final static String DEFAULT_IP_ADDRESS = "192.168.016.002";

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

    private static final long DEFAULT_DELAY = 250L; //todo check this delay! (10ms should be enough)

    public Presenter(View view){
        this.view = view;

        this.view.addSerialPorts(SerialPortConnection.getAvailableSerialPorts());

        ipAddress = DEFAULT_IP_ADDRESS;
        this.view.setDefaultIpAddress(ipAddress);
        useEthernetConnection(true);
    }

    public void useEthernetConnection(boolean use) {
        if (connection != null) {
            connection.close();
        }

        try {
            if (use) {
                connection = new EthernetConnection(PC_PORT, CS2_PORT, ipAddress);
            } else {
                connection = new SerialPortConnection(serialPort, BAUD, DATA_BITS, STOP_BITS, PARITY_BIT);
            }
        } catch (FrameworkException e) {
            view.showException(e);
        }
    }

    public void setLoc(String loc) {
        this.loc = Integer.parseInt(loc);
    }

    public void setSerialPort(String serialPort) {
        this.serialPort = serialPort;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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
        sendPacket(CS2CANCommands.toggleFunction(loc, LIGHT_FUNCTION, on ? CS2CANCommands.FUNCTION_ON : CS2CANCommands.FUNCTION_OFF));
    }

    public void sendVelocity(int velocity){
        sendPacket(CS2CANCommands.setVelocity(loc, velocity));
    }

    public void sendToggleDirection(){
        int[] velocity = new int[1];

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

        sendPacket(CS2CANCommands.queryVelocity(loc));
        pause(DEFAULT_DELAY);


        sendPacket(CS2CANCommands.setDirection(loc, CS2CANCommands.DIRECTION_TOGGLE));
        pause(DEFAULT_DELAY);

        addPacketListener(
                new PacketListener() {
                    @Override
                    public void packetEvent(PacketEvent packetEvent) {
                        byte command = packetEvent.getCANPacket().getCommand();

                        if ((command & 0xFE) != CS2CANCommands.DIRECTION) {
                            return;
                        }

                        byte[] data = packetEvent.getCANPacket().getData();
                        byte direction = data[4];

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

        sendPacket(CS2CANCommands.queryDirection(loc)); //query new direction
    }

    public void cleanUp(){
        connection.close();
    }

    private void sendQueryVelocity(){
        addPacketListener(
                new PacketListener() {
                    @Override
                    public void packetEvent(PacketEvent packetEvent) {
                        byte command = packetEvent.getCANPacket().getCommand();

                        if ((command & 0xFE) != CS2CANCommands.VELOCITY
                                && packetEvent.getCANPacket().getDlc() != CS2CANCommands.VELOCITY_SET_DLC) {
                            return;
                        }

                        byte[] data = packetEvent.getCANPacket().getData();
                        byte[] velocityBytes = new byte[]{data[4], data[5]};
                        int velocity = (((velocityBytes[0] & 0xFF) << 8) | (velocityBytes[1] & 0xFF));
                        view.setVelocity(velocity);

                        removePacketListener(this);
                    }
                }
        );

        sendPacket(CS2CANCommands.queryVelocity(loc));
    }

    public void sendGetLoks() {
        ConfigDataStreamPacketListener configDataStreamPacketListener = new ConfigDataStreamPacketListener() {
            @Override
            public void bytesDecompressed(final byte[] decompressedBytes) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < decompressedBytes.length; i++) {
                    stringBuilder.append((char) decompressedBytes[i]);
                }

                view.showConfigData(stringBuilder.toString());

                removePacketListener(this);
            }
        };

        configDataStreamPacketListener.addExceptionHandler(new ExceptionHandler() {
            @Override
            public void onException(final FrameworkException frameworkException) {
                view.showException(frameworkException);
                //frameworkException.getCause() //todo gets inner exception
            }
        });

        addPacketListener(configDataStreamPacketListener);

        sendPacket(CS2CANCommands.requestConfigData("loks"));
    }

    private void sendPacket(CANPacket canPacket) {
        try {
            connection.writeCANPacket(canPacket);
        } catch (FrameworkException e) {
            view.showException(e);
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
            view.showException(e);
        }
    }
}
