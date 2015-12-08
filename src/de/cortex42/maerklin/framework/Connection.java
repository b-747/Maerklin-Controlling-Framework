package de.cortex42.maerklin.framework;

/**
 * Created by ivo on 08.12.15.
 */
//Strategie
public interface Connection {
    void writeCANPacket(CANPacket canPacket) throws FrameworkException;

    void addPacketListener(PacketListener packetListener);

    void removePacketListener(PacketListener packetListener);
}
