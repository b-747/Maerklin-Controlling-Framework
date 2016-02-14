package bachelorarbeit.framework;

import bachelorarbeit.framework.packetlistener.PacketListener;

/**
 * Created by ivo on 08.12.15.
 */
public interface Connection extends AutoCloseable {
    void sendCANPacket(CANPacket canPacket) throws FrameworkException;

    void addPacketListener(PacketListener packetListener);

    void removePacketListener(PacketListener packetListener);

    @Override
    void close();
}
