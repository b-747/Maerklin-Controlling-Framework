package de.cortex42.maerklin.framework.packetlistener;

import de.cortex42.maerklin.framework.FrameworkException;

import java.util.EventListener;

/**
 * Created by ivo on 02.11.15.
 */
public abstract class PacketListener implements EventListener {
    public abstract void onPacketEvent(PacketEvent packetEvent);

    public abstract void onSuccess();

    public abstract void onException(FrameworkException frameworkException);
}