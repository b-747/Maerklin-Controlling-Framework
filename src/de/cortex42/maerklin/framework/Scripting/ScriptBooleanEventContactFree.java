package de.cortex42.maerklin.framework.Scripting;

import de.cortex42.maerklin.framework.*;

/**
 * Created by ivo on 20.11.15.
 */
public class ScriptBooleanEventContactFree implements BooleanEvent {
    private final ScriptContext scriptContext;
    private final int contactId;
    private final long freeTime;
    private final long timeout;
    private final static long DEFAULT_TIMEOUT = 60000; //60s

    public ScriptBooleanEventContactFree(ScriptContext scriptContext, int contactId, long freeTime, long timeout) {
        this.scriptContext = scriptContext;
        this.contactId = contactId;
        this.freeTime = freeTime;
        this.timeout = timeout;
    }

    public ScriptBooleanEventContactFree(ScriptContext scriptContext, int contactId, long freeTime) {
        this(scriptContext, contactId, freeTime, DEFAULT_TIMEOUT);
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
                        && (canPacket.getData()[5] & 0xFF) == CS2CANCommands.EQUIPMENT_POSITION_OFF) {

                    synchronized (waitingThreadExchangeObject) {
                        waitingThreadExchangeObject.value = true;
                        waitingThreadExchangeObject.notify();
                    }
                    scriptContext.removePacketListener(this);
                }
            }
        });

        while (!waitingThreadExchangeObject.value) { //wait for query response
            synchronized (waitingThreadExchangeObject) {
                try {
                    waitingThreadExchangeObject.wait(timeout);
                } catch (InterruptedException e) {
                    throw new FrameworkException(e);
                }
            }
        }

        waitingThreadExchangeObject.value = false; //reset and add another listener

        PacketListener packetListener = new PacketListener() {
            @Override
            public void packetEvent(PacketEvent packetEvent) {
                CANPacket canPacket = packetEvent.getCANPacket();

                if ((canPacket.getCommand() & 0xFE) == CS2CANCommands.S88_EVENT
                        && canPacket.getDlc() == CS2CANCommands.S88_EVENT_RESPONSE_DLC
                        && canPacket.getID() == contactId) {
                    waitingThreadExchangeObject.value = true;
                }
            }
        };
        scriptContext.addPacketListener(packetListener);

        try {
            Thread.sleep(freeTime); //now wait
        } catch (InterruptedException e) {
            throw new FrameworkException(e);
        }

        scriptContext.removePacketListener(packetListener);

        //if no S88 event occured until now (value is false), then the contact remained free
        return !waitingThreadExchangeObject.value;

    }
}
