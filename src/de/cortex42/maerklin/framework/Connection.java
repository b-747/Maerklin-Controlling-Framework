package de.cortex42.maerklin.framework;

import de.cortex42.maerklin.framework.packetlistener.PacketListener;

/**
 * Created by ivo on 08.12.15.
 */
//Strategie
public interface Connection extends AutoCloseable {
    void sendCANPacket(CANPacket canPacket) throws FrameworkException;

    void addPacketListener(PacketListener packetListener);

    void removePacketListener(PacketListener packetListener);

    @Override
    void close();
}
