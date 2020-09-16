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
public class ScriptBooleanEventContactReached implements BooleanEvent {
    private final ScriptContext scriptContext;
    private final int contactUid;
    private final long timeout;
    private final static long DEFAULT_TIMEOUT = 30000L; //30s
    private final Lock lock = new ReentrantLock();
    private final Condition condition;

    public ScriptBooleanEventContactReached(final ScriptContext scriptContext, final int contactUid, final long timeout) {
        if (scriptContext == null) {
            throw new IllegalArgumentException("scriptContext must not be null.");
        }
        this.scriptContext = scriptContext;
        this.contactUid = contactUid;
        this.timeout = timeout;
        condition = lock.newCondition();
    }

    public ScriptBooleanEventContactReached(final ScriptContext scriptContext, final int contactUid) {
        this(scriptContext, contactUid, DEFAULT_TIMEOUT);
    }

    @Override
    public boolean getAsBoolean() throws FrameworkException {
        final ThreadExchangeObject threadExchangeObject = new ThreadExchangeObject();

        final S88EventPacketListener s88EventPacketListener = new S88EventPacketListener(contactUid, S88EventPacketListener.ContactState.ACTIVATED) {
            @Override
            public void onSuccess() {
                lock.lock();
                try {
                    threadExchangeObject.value = true;
                    condition.signal();
                } finally {
                    lock.unlock();
                }
            }
        };

        scriptContext.addPacketListener(s88EventPacketListener);

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

        return threadExchangeObject.value;
    }
}
