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

    private final CANPacketListener canPacketListener = new CANPacketListener();

    public SerialPortConnection(final String systemPortName, final int baud, final int dataBits, final int stopBits, final int parityBit) throws SerialPortException {
        serialPort = SerialPort.getCommPort(systemPortName);

        serialPort.setComPortParameters(baud, dataBits, stopBits, parityBit);
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_CTS_ENABLED); //todo

        if (!serialPort.openPort()) {
            throw new SerialPortException("Could not open the serial port.");
        }
    }

    public static ArrayList<String> getAvailableSerialPorts() {
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        ArrayList<String> portNames = new ArrayList<>();

        for (SerialPort serialPort : serialPorts) {
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
        serialPort.addDataListener(canPacketListener); //happens only once
        canPacketListener.addPacketListener(packetListener);
    }

    synchronized public void removePacketListener(final PacketListener packetListener) {
        canPacketListener.removePacketListener(packetListener);

        if (!canPacketListener.packetListenersAvailable()) {
            serialPort.removeDataListener();
        }
    }

    synchronized public void writeCANPacket(final CANPacket canPacket) throws SerialPortException {
        byte[] bytesToWrite = canPacket.getBytes();

        int bytesWritten = serialPort.writeBytes(bytesToWrite, bytesToWrite.length);

        if (bytesWritten != bytesToWrite.length) {
            throw new SerialPortException("Not all bytes were written. Check serial port connection.");
        }
    }

    private final class CANPacketListener implements SerialPortPacketListener {
        private final ArrayList<PacketListener> packetListeners = new ArrayList<>();

        public CANPacketListener() {
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

            byte[] data = serialPortEvent.getReceivedData();
            CANPacket canPacket = new CANPacket(data);

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
