package bachelorarbeit.framework.scripting;

import bachelorarbeit.framework.CANPacket;
import bachelorarbeit.framework.Connection;
import bachelorarbeit.framework.FrameworkException;
import bachelorarbeit.framework.packetlistener.PacketListener;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptContext {
    private final Connection connection;

    public ScriptContext(final Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("connection must not be null.");
        }

        this.connection = connection;
    }

    public void sendCANPacket(final CANPacket canPacket) throws FrameworkException {
        connection.sendCANPacket(canPacket);
    }

    public void addPacketListener(final PacketListener packetListener) {
        connection.addPacketListener(packetListener);
    }

    public void removePacketListener(final PacketListener packetListener) {
        connection.removePacketListener(packetListener);
    }
}
