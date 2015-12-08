package de.cortex42.maerklin.framework;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;

import java.util.ArrayList;

/**
 * Created by ivo on 21.10.15.
 */
//singleton
public class SerialPortInterface {

    private SerialPort serialPort = null;

    private final CANPacketListener canPacketListener = new CANPacketListener();

    private final static SerialPortInterface instance = new SerialPortInterface();

    synchronized public static SerialPortInterface getInstance() {
        return instance;
    }

    private SerialPortInterface(){
    }

    synchronized public ArrayList<String> getAvailableSerialPorts() {
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        ArrayList<String> portNames = new ArrayList<>();

        for (SerialPort serialPort: serialPorts) {
            portNames.add(serialPort.getSystemPortName());
        }

        return portNames;
    }

    //Parameters for CC-Schnitte
    /*
        private final static int BAUD = 500000;
        private final static int DATA_BITS = 8;
        private final static int STOP_BITS = 1;
        private final static int PARITY_BIT = 0;
     */
    synchronized public boolean openPort(String systemPortName, int baud, int dataBits, int stopBits, int parityBit) {
        serialPort = SerialPort.getCommPort(systemPortName);

        serialPort.setComPortParameters(baud, dataBits, stopBits, parityBit);
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_CTS_ENABLED);

        return serialPort.openPort();
    }

    synchronized public boolean closePort() {
        return serialPort == null || serialPort.closePort();
    }

    synchronized public void addPacketListener(PacketListener packetListener) {
        serialPort.addDataListener(canPacketListener); //happens only once
        canPacketListener.addPacketListener(packetListener);
    }

    synchronized public void removePacketListener(PacketListener packetListener) {
        canPacketListener.removePacketListener(packetListener);

        if (!canPacketListener.packetListenersAvailable()) {
            serialPort.removeDataListener();
        }
    }

    /**
     * @param canPacket
     * @return false if not every byte was written
     */
    synchronized public void writeCANPacket(CANPacket canPacket) throws FrameworkException {
        byte[] bytesToWrite = canPacket.getBytes();

        int bytesWritten = serialPort.writeBytes(bytesToWrite, bytesToWrite.length);

        if (bytesWritten != bytesToWrite.length) {
            throw new FrameworkException("Not all bytes were written. Check serial port connection.");
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
        public void serialEvent(SerialPortEvent serialPortEvent) {
            if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
                return;
            }

            byte[] data = serialPortEvent.getReceivedData();
            CANPacket canPacket = new CANPacket(data);

            for (int i = 0; i < packetListeners.size(); i++) {
                packetListeners.get(i).packetEvent(new PacketEvent(canPacket));
            }

        }

        public void addPacketListener(PacketListener packetListener){
            if(!packetListeners.contains(packetListener)) {
                packetListeners.add(packetListener);
            }
        }

        public void removePacketListener(PacketListener packetListener){
            packetListeners.remove(packetListener);
        }

        public boolean packetListenersAvailable() {
            return !packetListeners.isEmpty();
        }
    }
}
