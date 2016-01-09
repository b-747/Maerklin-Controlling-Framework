package de.cortex42.maerklin.framework;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import de.cortex42.maerklin.framework.packetlistener.PacketEvent;
import de.cortex42.maerklin.framework.packetlistener.PacketListener;

import java.util.ArrayList;

/**
 * Created by ivo on 21.10.15.
 */
//Konkrete Strategie
public class SerialPortConnection implements Connection {
    private final SerialPort serialPort;
    private final ConcreteSerialPortPacketListener concreteSerialPortPacketListener = new ConcreteSerialPortPacketListener();

    /*
    * Settings for CC-Schnitte 2.1
    * http://can-digital-bahn.com/modul.php?system=sys5&modul=54
    */
    private static final int BAUD_RATE = 500000;
    private static final int DATA_BITS = 8;
    private static final int STOP_BITS = SerialPort.ONE_STOP_BIT;
    private static final int PARITY = SerialPort.NO_PARITY;
    private static final int FLOW_CONTROL = SerialPort.FLOW_CONTROL_CTS_ENABLED | SerialPort.FLOW_CONTROL_RTS_ENABLED;


    public SerialPortConnection(final String systemPortName) throws SerialPortException {
        serialPort = SerialPort.getCommPort(systemPortName);

        serialPort.setComPortParameters(BAUD_RATE, DATA_BITS, STOP_BITS, PARITY);
        serialPort.setFlowControl(FLOW_CONTROL); //todo test

        if (!serialPort.openPort()) {
            throw new SerialPortException("Could not open the serial port.");
        }
    }

    public static ArrayList<String> getAvailableSerialPorts() {
        final SerialPort[] serialPorts = SerialPort.getCommPorts();
        final ArrayList<String> portNames = new ArrayList<>();

        for (final SerialPort serialPort : serialPorts) {
            portNames.add(serialPort.getSystemPortName());
        }

        return portNames;
    }

    @Override
    synchronized public void close() {
        if (serialPort != null) {
            serialPort.closePort();
        }
    }

    synchronized public void addPacketListener(final PacketListener packetListener) {
        serialPort.addDataListener(concreteSerialPortPacketListener); //happens only once
        concreteSerialPortPacketListener.addPacketListener(packetListener);
    }

    synchronized public void removePacketListener(final PacketListener packetListener) {
        concreteSerialPortPacketListener.removePacketListener(packetListener);

        if (!concreteSerialPortPacketListener.packetListenersAvailable()) {
            serialPort.removeDataListener();
        }
    }

    synchronized public void writeCANPacket(final CANPacket canPacket) throws SerialPortException {
        final byte[] bytesToWrite = canPacket.getBytes();

        final int bytesWritten = serialPort.writeBytes(bytesToWrite, bytesToWrite.length);

        if (bytesWritten != bytesToWrite.length) {
            throw new SerialPortException("Not all bytes were written. Check serial port connection.");
        }
    }

    private final class ConcreteSerialPortPacketListener implements SerialPortPacketListener {
        private final ArrayList<PacketListener> packetListeners = new ArrayList<>();

        public ConcreteSerialPortPacketListener() {
        }

        @Override
        public int getPacketSize() {
            return CANPacket.CAN_PACKET_SIZE;
        }

        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
        }

        @Override
        public void serialEvent(final SerialPortEvent serialPortEvent) {
            if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
                return;
            }

            final byte[] data = serialPortEvent.getReceivedData();
            final CANPacket canPacket = new CANPacket(data);

            for (int i = 0; i < packetListeners.size(); i++) {
                packetListeners.get(i).onPacketEvent(new PacketEvent(canPacket));
            }

        }

        public void addPacketListener(final PacketListener packetListener) {
            if (!packetListeners.contains(packetListener)) {
                packetListeners.add(packetListener);
            }
        }

        public void removePacketListener(final PacketListener packetListener) {
            packetListeners.remove(packetListener);
        }

        public boolean packetListenersAvailable() {
            return !packetListeners.isEmpty();
        }
    }
}
