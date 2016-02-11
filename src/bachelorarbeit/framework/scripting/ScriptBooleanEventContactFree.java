package bachelorarbeit.framework.scripting;

import bachelorarbeit.framework.FrameworkException;
import bachelorarbeit.framework.packetlistener.S88EventPacketListener;

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
    private final static long DEFAULT_TIMEOUT = 30000L; //30s
    private final Lock lock = new ReentrantLock();
    private final Condition condition;

    public ScriptBooleanEventContactFree(final ScriptContext scriptContext, final int contactId, final long freeTime, final long timeout) {
        if (scriptContext == null) {
            throw new IllegalArgumentException("scriptContext must not be null.");
        }
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
        S88EventPacketListener s88EventPacketListener = new S88EventPacketListener(contactId, S88EventPacketListener.ContactState.DEACTIVATED) {
            @Override
            public void onSuccess() { //contact free
                lock.lock();
                try {
                    condition.signal();
                } finally {
                    lock.unlock();
                }
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
        } catch (final InterruptedException e) {
            throw new FrameworkException(e);
        } finally {
            scriptContext.removePacketListener(s88EventPacketListener);
            lock.unlock();
        }

        final ThreadExchangeObject threadExchangeObject = new ThreadExchangeObject();

        s88EventPacketListener = new S88EventPacketListener(contactId, S88EventPacketListener.ContactState.IRRELEVANT) { //here the position does not matter
            @Override
            public void onSuccess() {
                threadExchangeObject.value = true;
            }
        };

        scriptContext.addPacketListener(s88EventPacketListener);

        try {
            Thread.sleep(freeTime); //now wait
        } catch (final InterruptedException e) {
            throw new FrameworkException(e);
        } finally {
            scriptContext.removePacketListener(s88EventPacketListener);
        }

        //if no S88 event occurred until now (value is false), then the contact remained free
        return !threadExchangeObject.value;
    }
}
