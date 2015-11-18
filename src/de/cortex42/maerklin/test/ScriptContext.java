package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.CANPacket;
import de.cortex42.maerklin.framework.EthernetInterface;
import de.cortex42.maerklin.framework.PacketListener;
import de.cortex42.maerklin.framework.SerialPortInterface;

import java.io.IOException;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptContext {
    private final EthernetInterface ethernetInterface;
    private final SerialPortInterface serialPortInterface;
    private final String targetAddress;
    private final int targetPort;
    private final boolean useEthernetInterface;

    public ScriptContext(EthernetInterface ethernetInterface, String targetAddress, int targetPort) {
        this.ethernetInterface = ethernetInterface;
        this.targetAddress = targetAddress;
        this.targetPort = targetPort;
        this.useEthernetInterface = true;
        this.serialPortInterface = null;
    }

    public ScriptContext(SerialPortInterface serialPortInterface) {
        this.serialPortInterface = serialPortInterface;
        this.ethernetInterface = null;
        this.targetAddress = null;
        this.targetPort = -1;
        this.useEthernetInterface = false;
    }

    public void writeCANPacket(CANPacket canPacket) {
        if (useEthernetInterface) {
            try {
                ethernetInterface.writeCANPacket(canPacket, targetAddress, targetPort);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            serialPortInterface.writeCANPacket(canPacket);

        }
    }

    public void addPacketListener(PacketListener packetListener) {
        if (useEthernetInterface) {
            ethernetInterface.addPacketListener(packetListener);
        } else {
            serialPortInterface.addPacketListener(packetListener);
        }
    }

    public void removePacketListener(PacketListener packetListener) {
        if (useEthernetInterface) {
            ethernetInterface.removePacketListener(packetListener);
        } else {
            serialPortInterface.removePacketListener(packetListener);
        }
    }
}
