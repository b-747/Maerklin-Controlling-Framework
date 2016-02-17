package bachelorarbeit.framework;

import bachelorarbeit.framework.packetlistener.PacketEvent;
import bachelorarbeit.framework.packetlistener.PacketListener;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * Created by ivo on 06.11.15.
 */
public class EthernetConnection implements Connection {
    private final DatagramSocket datagramSocket;
    private static final int localPort = 15730;
    private static final int targetPort = 15731;
    private final InetAddress targetAddress;

    private final ArrayList<PacketListener> packetListeners = new ArrayList<>();
    private final ArrayList<ExceptionListener> exceptionListeners = new ArrayList<>();
    private volatile boolean isListening = false; //volatile for listening thread

    public EthernetConnection(final String targetAddress) throws FrameworkException {
        try {
            this.targetAddress = InetAddress.getByName(targetAddress);

            datagramSocket = new DatagramSocket(localPort);
        } catch (final SocketException | UnknownHostException e) {
            throw new FrameworkException(e);
        }
    }

    @Override
    synchronized public void close() {
        stopListening();
        datagramSocket.close();
    }

    public void sendCANPacket(final CANPacket canPacket) throws FrameworkException {
        final byte[] bytes = canPacket.getBytes();

        final DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, targetAddress, targetPort);

        try {
            datagramSocket.send(datagramPacket);
        } catch (final IOException e) {
            throw new FrameworkException(e);
        }
    }

    public void addExceptionListener(final ExceptionListener exceptionListener) {
        exceptionListeners.add(exceptionListener);
    }

    public void removeExceptionListener(final ExceptionListener exceptionListener) {
        exceptionListeners.remove(exceptionListener);
    }

    synchronized public void addPacketListener(final PacketListener packetListener) {
        packetListeners.add(packetListener);

        startListening();
    }

    synchronized public void removePacketListener(final PacketListener packetListener) {
        packetListeners.remove(packetListener);

        if (packetListeners.isEmpty()) {
            stopListening();
        }
    }

    private void startListening() {
        if (!isListening) {
            isListening = true;

            final Thread thread = new Thread(new Runnable() {
                public void run() {
                    while (isListening) {
                        final DatagramPacket datagramPacket = new DatagramPacket(new byte[CANPacket.CAN_PACKET_SIZE], CANPacket.CAN_PACKET_SIZE);

                        try {
                            datagramSocket.receive(datagramPacket);
                        } catch (final IOException e) {
                            final FrameworkException frameworkException = new FrameworkException(e);

                            //call exception listeners
                            for (int i = 0; i < exceptionListeners.size(); i++) {
                                exceptionListeners.get(i).onException(frameworkException);
                            }

                            break;
                        }

                        final CANPacket canPacket = new CANPacket(datagramPacket.getData());

                        for (int i = 0; i < packetListeners.size(); i++) {
                            packetListeners.get(i).onPacketEvent(new PacketEvent(canPacket));
                        }
                    }
                }
            });

            thread.start();
        }
    }

    private void stopListening() {
        isListening = false;
    }


}
