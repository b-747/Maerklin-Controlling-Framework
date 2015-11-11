package de.cortex42.maerklin.framework;

import java.util.EventObject;

/**
 * Created by ivo on 04.11.15.
 */
public class PacketEvent extends EventObject{
    private final CANPacket canPacket;

    public PacketEvent(CANPacket canPacket) {
        super(canPacket);
        this.canPacket = canPacket;
    }

    public CANPacket getCANPacket(){
        return canPacket;
    }
}
