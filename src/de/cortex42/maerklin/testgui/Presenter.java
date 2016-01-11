package de.cortex42.maerklin.testgui;

import de.cortex42.maerklin.framework.*;
import de.cortex42.maerklin.framework.packetlistener.ConfigDataStreamPacketListener;
import de.cortex42.maerklin.framework.packetlistener.DirectionPacketListener;
import de.cortex42.maerklin.framework.packetlistener.PacketListener;

import java.util.ArrayList;

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

/* //todo
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

    private static final long DEFAULT_DELAY = 10L; //todo 10ms are enough

    public Presenter(final View view) {
        this.view = view;

        final ArrayList<String> serialPorts = SerialPortConnection.getAvailableSerialPorts();

        this.view.addSerialPorts(serialPorts);
        if (!serialPorts.isEmpty()) {
            this.serialPort = serialPorts.get(0);
        }

        ipAddress = DEFAULT_IP_ADDRESS;
        this.view.setDefaultIpAddress(ipAddress);
        useEthernetConnection(true);
    }

    public void useEthernetConnection(final boolean use) {
        if (connection != null) {
            connection.close();
        }

        try {
            if (use) {
                connection = new EthernetConnection(PC_PORT, CS2_PORT, ipAddress);
            } else {
                connection = new SerialPortConnection(serialPort);
            }
        } catch (final FrameworkException e) {
            view.showException(e);
        }
    }

    public void setLoc(final String loc) {
        this.loc = Integer.parseInt(loc);
    }

    public void setSerialPort(final String serialPort) {
        this.serialPort = serialPort;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void sendStart() {
        sendPacket(CS2CANCommands.newRegistration());
        pause(DEFAULT_DELAY);
        sendPacket(CS2CANCommands.unlockRail());
        pause(DEFAULT_DELAY);

        sendPacket(CS2CANCommands.go());
    }

    public void sendStop() {
        sendPacket(CS2CANCommands.stop());
    }

    public void sendBootloaderGo() {
        sendPacket(CS2CANCommands.bootloaderGo());
        pause(400L); //pause 400ms after sending bootloader go command
    }

    public void sendLight(final boolean on) {
        sendPacket(CS2CANCommands.toggleFunction(loc, LIGHT_FUNCTION, on ? CS2CANCommands.FUNCTION_ON : CS2CANCommands.FUNCTION_OFF));
    }

    public void sendVelocity(final int velocity) {
        sendPacket(CS2CANCommands.setVelocity(loc, velocity));
    }

    public void sendToggleDirection() {
        /*final int[] velocity = new int[1];

        addPacketListener(
                new VelocityPacketListener() {
                    @Override
                    public void onSuccess() {
                        velocity[0] = getVelocity();
                        removePacketListener(this);
                    }
                }
        );*/

        //sendPacket(CS2CANCommands.queryVelocity(loc));
        //pause(DEFAULT_DELAY);

        sendPacket(CS2CANCommands.setDirection(loc, CS2CANCommands.DIRECTION_TOGGLE));
        pause(DEFAULT_DELAY);

        addPacketListener(
                new DirectionPacketListener() {
                    @Override
                    public void onSuccess() {
                        if (getDirection() != Direction.TOGGLE) {
                            view.setDirection(getDirection().name());
                            removePacketListener(this);
                        }
                    }
                }
        );

        //sendVelocity(velocity[0]);
        //pause(DEFAULT_DELAY);

        //sendStart();
        //pause(DEFAULT_DELAY);

        sendPacket(CS2CANCommands.queryDirection(loc)); //query new direction
    }

    public void cleanUp() {
        connection.close();
    }

    public void sendGetLocos() {
        addPacketListener(new ConfigDataStreamPacketListener() {
            @Override
            public void onSuccess() {
                final byte[] decompressedBytes = getDecompressedBytes();

                final StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < decompressedBytes.length; i++) {
                    stringBuilder.append((char) decompressedBytes[i]);
                }

                view.showConfigData(stringBuilder.toString());

                removePacketListener(this);
            }

            @Override
            public void onException(final FrameworkException frameworkException) {
                view.showException(frameworkException);
                //frameworkException.getCause() //todo gets inner exception
            }
        });

        sendPacket(CS2CANCommands.requestConfigData("loks"));
    }

    private void sendPacket(final CANPacket canPacket) {
        try {
            connection.sendCANPacket(canPacket);
        } catch (final FrameworkException e) {
            view.showException(e);
        }
    }

    private void addPacketListener(final PacketListener packetListener) {
        connection.addPacketListener(packetListener);
    }

    private void removePacketListener(final PacketListener packetListener) {
        connection.removePacketListener(packetListener);
    }

    private void pause(final long ms) {
        try {
            Thread.sleep(ms);
        } catch (final InterruptedException e) {
            view.showException(e);
        }
    }
}
