package de.cortex42.maerklin.framework.Scripting;

import de.cortex42.maerklin.framework.*;

/**
 * Created by ivo on 20.11.15.
 */
public class ScriptBooleanEventContactReached implements BooleanEvent {
    private final ScriptContext scriptContext;
    private final int contactId;
    private final long timeout;
    private final static long DEFAULT_TIMEOUT = 60000; //60s

    public ScriptBooleanEventContactReached(ScriptContext scriptContext, int contactId, long timeout) {
        this.contactId = contactId;
        this.scriptContext = scriptContext;
        this.timeout = timeout;
    }

    public ScriptBooleanEventContactReached(ScriptContext scriptContext, int contactId) {
        this(scriptContext, contactId, DEFAULT_TIMEOUT);
    }

    @Override
    public boolean getAsBoolean() throws FrameworkException {
        return check();
    }

    private boolean check() throws FrameworkException {
        final WaitingThreadExchangeObject waitingThreadExchangeObject = new WaitingThreadExchangeObject();

        scriptContext.addPacketListener(new PacketListener() {
            @Override
            public void packetEvent(PacketEvent packetEvent) {
                CANPacket canPacket = packetEvent.getCANPacket();

                if ((canPacket.getCommand() & 0xFE) == CS2CANCommands.S88_EVENT
                        && canPacket.getDlc() == CS2CANCommands.S88_EVENT_RESPONSE_DLC
                        && canPacket.getID() == contactId
                        && ((canPacket.getData()[5] & 0xFF) == CS2CANCommands.EQUIPMENT_POSITION_ON)) {

                    synchronized (waitingThreadExchangeObject) {
                        waitingThreadExchangeObject.value = true;
                        waitingThreadExchangeObject.notify();
                    }
                    scriptContext.removePacketListener(this);
                }
            }
        });

        while (!waitingThreadExchangeObject.value) {
            synchronized (waitingThreadExchangeObject) {
                try {
                    waitingThreadExchangeObject.wait(timeout);
                } catch (InterruptedException e) {
                    throw new FrameworkException(e);
                }
            }
        }

        return waitingThreadExchangeObject.value;
    }
}
