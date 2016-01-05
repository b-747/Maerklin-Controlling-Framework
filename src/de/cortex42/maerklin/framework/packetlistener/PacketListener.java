package de.cortex42.maerklin.framework.packetlistener;

import de.cortex42.maerklin.framework.FrameworkException;

import java.util.EventListener;

/**
 * Created by ivo on 02.11.15.
 */
public interface PacketListener extends EventListener {
    void onPacketEvent(PacketEvent packetEvent);

    void onSuccess();

    void onException(FrameworkException frameworkException);
}