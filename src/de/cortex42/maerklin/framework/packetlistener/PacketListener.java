package de.cortex42.maerklin.framework.packetlistener;

import de.cortex42.maerklin.framework.ExceptionListener;

import java.util.EventListener;

/**
 * Created by ivo on 02.11.15.
 */
public abstract class PacketListener implements EventListener {
    protected ExceptionListener exceptionListener = null;

    public void setExceptionListener(final ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }

    public abstract void onPacketEvent(PacketEvent packetEvent);

    public abstract void onSuccess();
}