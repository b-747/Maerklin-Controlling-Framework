package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.CANPacket;
import de.cortex42.maerklin.framework.Connection;
import de.cortex42.maerklin.framework.FrameworkException;
import de.cortex42.maerklin.framework.packetlistener.PacketListener;

/**
 * Created by ivo on 18.11.15.
 */
//Kontext
public class ScriptContext {
    private final Connection connection;

    public ScriptContext(final Connection connection) {
        if(connection == null){
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
