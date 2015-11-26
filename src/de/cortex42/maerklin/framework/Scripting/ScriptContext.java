package de.cortex42.maerklin.framework.Scripting;

import de.cortex42.maerklin.framework.*;

//todo strategy pattern

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

    public void writeCANPacket(CANPacket canPacket) throws FrameworkException {
        if (useEthernetInterface) {
            ethernetInterface.writeCANPacket(canPacket, targetAddress, targetPort);
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

    public void addEthernetInterfacePacketListenerExceptionHandler(EthernetInterfacePacketListenerExceptionHandler ethernetInterfacePacketListenerExceptionHandler) {
        if (useEthernetInterface) {
            ethernetInterface.addEthernetInterfacePacketListenerExceptionHandler(ethernetInterfacePacketListenerExceptionHandler);
        }
    }

    public void removeEthernetInterfacePacketListenerExceptionHandler(EthernetInterfacePacketListenerExceptionHandler ethernetInterfacePacketListenerExceptionHandler) {
        if (useEthernetInterface) {
            ethernetInterface.removeEthernetInterfacePacketListenerExceptionHandler(ethernetInterfacePacketListenerExceptionHandler);
        }
    }
}
