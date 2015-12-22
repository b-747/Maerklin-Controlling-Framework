package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.FrameworkException;
import de.cortex42.maerklin.framework.packetlistener.S88EventPacketListener;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptElementWaitForContact extends ScriptElement {
    private final int contactId;
    private final boolean positionOn;
    private final long timeout;
    private final static long DEFAULT_TIMEOUT = 60000L; //60s
    private final Lock lock = new ReentrantLock();
    private final Condition condition;

    public ScriptElementWaitForContact(final int contactId, final boolean positionOn) {
        this(contactId, positionOn, DEFAULT_TIMEOUT);
    }

    public ScriptElementWaitForContact(final int contactId, final boolean positionOn, final long timeout) {
        this.contactId = contactId;
        this.positionOn = positionOn;
        this.timeout = timeout;
        condition = lock.newCondition();
    }

    @Override
    public void executeElement(final ScriptContext scriptContext) throws FrameworkException {

        final S88EventPacketListener s88EventPacketListener = new S88EventPacketListener(contactId, positionOn) {
            @Override
            public void onSuccess() {
                lock.lock();
                try {
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
                throw new ScriptElementWaitTimeoutException();
            }
        } catch (final InterruptedException e) {
            throw new FrameworkException(e);
        } finally {
            lock.unlock();
            scriptContext.removePacketListener(s88EventPacketListener);
        }
    }
}
