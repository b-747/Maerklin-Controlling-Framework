package de.cortex42.maerklin.framework.packetlistener;

import de.cortex42.maerklin.framework.CANPacket;
import de.cortex42.maerklin.framework.CS2CANCommands;

/**
 * Created by ivo on 16.12.15.
 */
public abstract class VelocityPacketListener implements PacketListener {
    @Override
    public void packetEvent(final PacketEvent packetEvent) {
        CANPacket canPacket = packetEvent.getCANPacket();

        if ((canPacket.getCommand() & 0xFE) == CS2CANCommands.VELOCITY) {
            byte[] data = canPacket.getData();

            int velocity = ((data[4] & 0xFF) << 8 | (data[5] & 0xFF));


        }
    }

    public abstract void callback();
}
