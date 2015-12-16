package de.cortex42.maerklin.framework.packetlistener;

import java.util.EventListener;

/**
 * Created by ivo on 02.11.15.
 */
public interface PacketListener extends EventListener {
    void packetEvent(PacketEvent packetEvent);
}

//todo add packetlisteners for velocity, direction, function