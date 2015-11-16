package de.cortex42.maerklin.framework;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by ivo on 06.11.15.
 */
//singleton
public class EthernetInterface {
    private DatagramSocket datagramSocket = null;

    private final ArrayList<PacketListener> packetListeners = new ArrayList<>();
    private boolean isListening = false;
    private Thread thread = null;

    private static EthernetInterface instance = null;
    public static EthernetInterface getInstance(int port) throws SocketException {
        if(instance == null){
            instance = new EthernetInterface(port);
        }

        return instance;
    }

    private EthernetInterface(int port) throws SocketException {
        datagramSocket = new DatagramSocket(port);
    }

    public void cleanUp(){
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
    public void writeCANPacket(CANPacket canPacket, String targetAddress, int port) throws IOException {
        byte[] bytes = canPacket.getBytes();

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(targetAddress), port);

        datagramSocket.send(datagramPacket);
    }

    public void addPacketListener(PacketListener packetListener){
        if(!packetListeners.contains(packetListener)) {
            packetListeners.add(packetListener);
        }
        startListening();
    }

    public void removePacketListener(PacketListener packetListener){
        packetListeners.remove(packetListener);

        if(packetListeners.size() == 0){
            stopListening();
        }
    }

    private void startListening(){
        if(!isListening) {
            isListening = true;

            (thread = new Thread(new Runnable() {
                public void run() {
                    while (isListening) {
                        DatagramPacket datagramPacket = new DatagramPacket(new byte[CANPacket.CAN_PACKET_SIZE], CANPacket.CAN_PACKET_SIZE);

                        try {
                            datagramSocket.receive(datagramPacket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        CANPacket canPacket = new CANPacket(datagramPacket.getData());

                        for (int i = 0; i < packetListeners.size(); i++) {
                            packetListeners.get(i).packetEvent(new PacketEvent(canPacket));
                        }
                    }
                }
            })).start();
        }
    }

    private void stopListening(){
        isListening=false;
    }
}
