package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ivo on 20.11.15.
 */
public class ScriptBooleanEventContactReached implements BooleanEvent {
    private final ScriptContext scriptContext;
    private final int contactId;
    private final long timeout;
    private final static long DEFAULT_TIMEOUT = 60000; //60s
    private final Lock lock = new ReentrantLock();
    private final Condition condition;

    public ScriptBooleanEventContactReached(ScriptContext scriptContext, int contactId, long timeout) {
        this.contactId = contactId;
        this.scriptContext = scriptContext;
        this.timeout = timeout;
        condition = lock.newCondition();
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

        PacketListener packetListener = new PacketListener() {
            @Override
            public void packetEvent(PacketEvent packetEvent) {
                CANPacket canPacket = packetEvent.getCANPacket();

                if ((canPacket.getCommand() & 0xFE) == CS2CANCommands.S88_EVENT
                        && canPacket.getDlc() == CS2CANCommands.S88_EVENT_RESPONSE_DLC
                        && canPacket.getID() == contactId
                        && ((canPacket.getData()[5] & 0xFF) == CS2CANCommands.EQUIPMENT_POSITION_ON)) {

                    lock.lock();
                    waitingThreadExchangeObject.value = true;
                    condition.signal();
                    lock.unlock();
                }
            }
        };

        scriptContext.addPacketListener(packetListener);

        lock.lock();
        try {
            if (!condition.await(timeout, TimeUnit.MILLISECONDS)) {
                //timeout
                return false;
            }
        } catch (InterruptedException e) {
            throw new FrameworkException(e);
        } finally {
            lock.unlock();
            scriptContext.removePacketListener(packetListener);
        }

        return waitingThreadExchangeObject.value;
    }
}
