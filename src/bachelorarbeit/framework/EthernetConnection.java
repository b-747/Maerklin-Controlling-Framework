package bachelorarbeit.framework;

import bachelorarbeit.framework.packetlistener.PacketEvent;
import bachelorarbeit.framework.packetlistener.PacketListener;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * Created by ivo on 06.11.15.
 */
//Konkrete Strategie
public class EthernetConnection implements Connection {
    private final DatagramSocket datagramSocket;
    private final int targetPort;
    private final InetAddress targetAddress;

    private final ArrayList<PacketListener> packetListeners = new ArrayList<>();
    private final ArrayList<ExceptionListener> exceptionListeners = new ArrayList<>();
    private volatile boolean isListening = false; //volatile for listening thread

    public EthernetConnection(final int localPort, final int targetPort, final String targetAddress) throws FrameworkException {
        this.targetPort = targetPort;

        try {
            this.targetAddress = InetAddress.getByName(targetAddress);

            datagramSocket = new DatagramSocket(localPort);
        } catch (final SocketException | UnknownHostException e) {
            throw new FrameworkException(e);
        }
    }

    @Override
    synchronized public void close() { //todo nötig
        stopListening();
        datagramSocket.close();
    }

    public void sendCANPacket(final CANPacket canPacket) throws FrameworkException { //todo synchronized hier nicht nötig (wird intern verwendet)
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

    synchronized public void addPacketListener(final PacketListener packetListener) { //todo nötig aufgrund removePacketListener (während remove keine andere synchronized-Methode aufrufbar)
        packetListeners.add(packetListener);

        startListening();
    }

    synchronized public void removePacketListener(final PacketListener packetListener) { //todo nötig
        packetListeners.remove(packetListener);

        if (packetListeners.isEmpty()) { //todo deswegen: erster Thread entfernt Listener, stellt fest, dass kein Listener mehr da ist => switch => anderer Thread fügt Listener hinzu => switch stopListening() obwohl noch ein anderer vorhanden ist
            stopListening();
        }
    }

    private void startListening() { //todo nicht nötig (wird nur von synchronized addPacketListener aufgerufen)
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
    } //todo nicht nötig (auch nur von synchronized)


}
