package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.FrameworkException;
import de.cortex42.maerklin.framework.packetlistener.S88EventPacketListener;

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
    private final static long DEFAULT_TIMEOUT = 60000L; //60s
    private final Lock lock = new ReentrantLock();
    private final Condition condition;

    public ScriptBooleanEventContactReached(final ScriptContext scriptContext, final int contactId, final long timeout) {
        this.contactId = contactId;
        this.scriptContext = scriptContext;
        this.timeout = timeout;
        condition = lock.newCondition();
    }

    public ScriptBooleanEventContactReached(final ScriptContext scriptContext, final int contactId) {
        this(scriptContext, contactId, DEFAULT_TIMEOUT);
    }

    @Override
    public boolean getAsBoolean() throws FrameworkException {
        return check();
    }

    private boolean check() throws FrameworkException {
        WaitingThreadExchangeObject waitingThreadExchangeObject = new WaitingThreadExchangeObject();

        S88EventPacketListener s88EventPacketListener = new S88EventPacketListener(contactId, true) {
            @Override
            public void onSuccess() {
                lock.lock();
                waitingThreadExchangeObject.value = true;
                condition.signal();
                lock.unlock();
            }
        };

        scriptContext.addPacketListener(s88EventPacketListener);

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
            scriptContext.removePacketListener(s88EventPacketListener);
        }

        return waitingThreadExchangeObject.value;
    }
}
