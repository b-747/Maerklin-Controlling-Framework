package de.cortex42.maerklin.framework;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * Created by ivo on 06.11.15.
 */
//singleton
public class EthernetInterface {
    private DatagramSocket datagramSocket = null;

    private final ArrayList<PacketListener> packetListeners = new ArrayList<>();
    private final ArrayList<EthernetInterfacePacketListenerExceptionHandler> ethernetInterfacePacketListenerExceptionHandlers = new ArrayList<>();
    private boolean isListening = false;

    private static EthernetInterface instance = null;

    synchronized public static EthernetInterface getInstance(int port) throws FrameworkException {
        if(instance == null){
            instance = new EthernetInterface(port);
        }

        return instance;
    }

    private EthernetInterface(int port) throws FrameworkException {
        try {
            datagramSocket = new DatagramSocket(port);
        } catch (SocketException e) {
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
     * @param targetAddress
     * @param port
     * @throws IOException, UnknownHostException, SocketException
     */
    synchronized public void writeCANPacket(CANPacket canPacket, String targetAddress, int port) throws FrameworkException {
        byte[] bytes = canPacket.getBytes();

        DatagramPacket datagramPacket;
        try {
            datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(targetAddress), port);
        } catch (UnknownHostException e) {
            throw new FrameworkException(e);
        }

        try {
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            throw new FrameworkException(e);
        }
    }

    synchronized public void addEthernetInterfacePacketListenerExceptionHandler(EthernetInterfacePacketListenerExceptionHandler ethernetInterfacePacketListenerExceptionHandler) {
        if (!ethernetInterfacePacketListenerExceptionHandlers.contains(ethernetInterfacePacketListenerExceptionHandler)) {
            ethernetInterfacePacketListenerExceptionHandlers.add(ethernetInterfacePacketListenerExceptionHandler);
        }
    }

    synchronized public void removeEthernetInterfacePacketListenerExceptionHandler(EthernetInterfacePacketListenerExceptionHandler ethernetInterfacePacketListenerExceptionHandler) {
        ethernetInterfacePacketListenerExceptionHandlers.remove(ethernetInterfacePacketListenerExceptionHandler);
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

                            //inform packetlistener users about error
                            for (int i = 0; i < ethernetInterfacePacketListenerExceptionHandlers.size(); i++) {
                                ethernetInterfacePacketListenerExceptionHandlers.get(i).onPacketListenerException(frameworkException);
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
