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
public class ScriptBooleanEventContactFree implements BooleanEvent {
    private final ScriptContext scriptContext;
    private final int contactId;
    private final long freeTime;
    private final long timeout;
    private final static long DEFAULT_TIMEOUT = 60000L; //60s
    private final Lock lock = new ReentrantLock();
    private final Condition condition;

    public ScriptBooleanEventContactFree(final ScriptContext scriptContext, final int contactId, final long freeTime, final long timeout) {
        this.scriptContext = scriptContext;
        this.contactId = contactId;
        this.freeTime = freeTime;
        this.timeout = timeout;
        condition = lock.newCondition();
    }

    public ScriptBooleanEventContactFree(final ScriptContext scriptContext, final int contactId, final long freeTime) {
        this(scriptContext, contactId, freeTime, DEFAULT_TIMEOUT);
    }

    @Override
    public boolean getAsBoolean() throws FrameworkException {
        return check();
    }

    private boolean check() throws FrameworkException {
        WaitingThreadExchangeObject waitingThreadExchangeObject = new WaitingThreadExchangeObject();

        S88EventPacketListener s88EventPacketListener = new S88EventPacketListener(contactId, false) {
            @Override
            public void onSuccess() { //contact free
                lock.lock();
                waitingThreadExchangeObject.value = true;
                condition.signal();
                lock.unlock();
            }
        };

        scriptContext.addPacketListener(s88EventPacketListener);

        //wait until contact is free
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

        waitingThreadExchangeObject.value = false; //reset and add another listener

        s88EventPacketListener = new S88EventPacketListener(contactId) { //here the position does not matter, so use the second constructor
            @Override
            public void onSuccess() {
                waitingThreadExchangeObject.value = true;
            }
        };

        scriptContext.addPacketListener(s88EventPacketListener);

        try {
            Thread.sleep(freeTime); //now wait
        } catch (InterruptedException e) {
            throw new FrameworkException(e);
        } finally {
            scriptContext.removePacketListener(s88EventPacketListener);
        }

        //if no S88 event occured until now (value is false), then the contact remained free
        return !waitingThreadExchangeObject.value;
    }
}
