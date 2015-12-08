package de.cortex42.maerklin.framework;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * Created by ivo on 06.11.15.
 */
public class EthernetInterface {
    private final DatagramSocket datagramSocket;
    private final int targetPort;
    private final InetAddress targetAddress;

    private final ArrayList<PacketListener> packetListeners = new ArrayList<>();
    private final ArrayList<ExceptionHandler> exceptionHandlers = new ArrayList<>();
    private boolean isListening = false;

    public EthernetInterface(int localPort, int targetPort, String targetAddress) throws FrameworkException {
        this.targetPort = targetPort;

        try {
            this.targetAddress = InetAddress.getByName(targetAddress);

            datagramSocket = new DatagramSocket(localPort);
        } catch (SocketException | UnknownHostException e) {
            throw new FrameworkException(e);
        }
    }

    synchronized public void cleanUp() {
        stopListening();
        datagramSocket.close();
    }

    /**
     * Sends a CANPacket as UDP packet to the target.
     * @param canPacket
     * @throws
     */
    synchronized public void writeCANPacket(CANPacket canPacket) throws FrameworkException {
        byte[] bytes = canPacket.getBytes();

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, targetAddress, targetPort);

        try {
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            throw new FrameworkException(e);
        }
    }

    synchronized public void addExceptionHandler(ExceptionHandler exceptionHandler) {
        if (!exceptionHandlers.contains(exceptionHandler)) {
            exceptionHandlers.add(exceptionHandler);
        }
    }

    synchronized public void removeExceptionHandler(ExceptionHandler exceptionHandler) {
        exceptionHandlers.remove(exceptionHandler);
    }

    synchronized public void addPacketListener(PacketListener packetListener) {
        if(!packetListeners.contains(packetListener)) {
            packetListeners.add(packetListener);
        }

        startListening();
    }

    synchronized public void removePacketListener(PacketListener packetListener) {
        packetListeners.remove(packetListener);

        if (packetListeners.isEmpty()) {
            stopListening();
        }
    }

    private void startListening(){
        if(!isListening) {
            isListening = true;

            Thread thread = new Thread(new Runnable() {
                public void run() {
                    while (isListening) {
                        DatagramPacket datagramPacket = new DatagramPacket(new byte[CANPacket.CAN_PACKET_SIZE], CANPacket.CAN_PACKET_SIZE);

                        try {
                            datagramSocket.receive(datagramPacket);
                        } catch (IOException e) {
                            FrameworkException frameworkException = new FrameworkException(e);
                            cleanUp(); //stop listening and close socket

                            //call exception handlers
                            for (int i = 0; i < exceptionHandlers.size(); i++) {
                                exceptionHandlers.get(i).onException(frameworkException);
                            }

                            break;
                        }

                        CANPacket canPacket = new CANPacket(datagramPacket.getData());

                        for (int i = 0; i < packetListeners.size(); i++) {
                            packetListeners.get(i).packetEvent(new PacketEvent(canPacket));
                        }
                    }
                }
            });

            thread.start();
        }
    }

    private void stopListening(){
        isListening=false;
    }
}
