package bachelorarbeit.testgui;

import bachelorarbeit.framework.*;
import bachelorarbeit.framework.packetlistener.ConfigDataStreamPacketListener;
import bachelorarbeit.framework.packetlistener.DirectionPacketListener;
import bachelorarbeit.framework.packetlistener.PacketListener;

import java.util.ArrayList;

/**
 * Created by ivo on 21.10.15.
 *
 * -----------PROTOTYPE!-----------
 */
public class Presenter {
    private final View view;
    private Connection connection;
    private int trainUid;
    private String ipAddress;
    private String serialPort;
    private final static String DEFAULT_IP_ADDRESS = "192.168.016.002";

    private static final int LIGHT_FUNCTION = 0;

    private static final int PC_PORT = 15730;
    private static final int CS2_PORT = 15731;

    private static final long DEFAULT_DELAY = 10L; //10ms

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

    public void setTrainUid(final String trainUid) {
        this.trainUid = Integer.parseInt(trainUid);
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
        sendPacket(CS2CANCommands.toggleFunction(trainUid, LIGHT_FUNCTION, on ? CS2CANCommands.FUNCTION_ON : CS2CANCommands.FUNCTION_OFF));
    }

    public void sendVelocity(final int velocity) {
        sendPacket(CS2CANCommands.setVelocity(trainUid, velocity));
    }

    public void sendToggleDirection() {
        sendPacket(CS2CANCommands.setDirection(trainUid, CS2CANCommands.DIRECTION_TOGGLE));
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

        sendPacket(CS2CANCommands.queryDirection(trainUid)); //query new direction
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
