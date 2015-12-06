package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptElementWaitForContact extends ScriptElement {
    private final int contactId;
    private final int switchOverTo;
    private final long timeout;
    private final static long DEFAULT_TIMEOUT = 60000L; //60s
    private final Lock lock = new ReentrantLock();
    private final Condition condition;

    public ScriptElementWaitForContact(int contactId, int switchOverTo) {
        this(contactId, switchOverTo, DEFAULT_TIMEOUT);
    }

    public ScriptElementWaitForContact(int contactId, int switchOverTo, long timeout) {
        this.contactId = contactId;
        this.switchOverTo = switchOverTo;
        this.timeout = timeout;
        condition = lock.newCondition();
    }

    @Override
    public void executeElement(final ScriptContext scriptContext) throws FrameworkException {
        final WaitingThreadExchangeObject waitingThreadExchangeObject = new WaitingThreadExchangeObject();

        scriptContext.addPacketListener(
                new PacketListener() {
                    @Override
                    public void packetEvent(PacketEvent packetEvent) {
                        CANPacket canPacket = packetEvent.getCANPacket();

                        if ((canPacket.getCommand() & 0xFE) == CS2CANCommands.S88_EVENT //for response bit
                                && canPacket.getDlc() == CS2CANCommands.S88_EVENT_RESPONSE_DLC
                                && canPacket.getID() == contactId
                                && ((canPacket.getData()[5] & 0xFF) == switchOverTo)) {

                            lock.lock();
                            waitingThreadExchangeObject.value = true;
                            condition.signal();
                            lock.unlock();

                            scriptContext.removePacketListener(this);
                        }
                    }
                });

        lock.lock();
        try {
            if (!condition.await(timeout, TimeUnit.MILLISECONDS)) {
                //timeout
                throw new FrameworkException("Timeout");
            }
        } catch (InterruptedException e) {
            throw new FrameworkException(e);
        } finally {
            lock.unlock();
        }
    }
}
