package bachelorarbeit.framework.scripting;

import bachelorarbeit.framework.FrameworkException;
import bachelorarbeit.framework.packetlistener.S88EventPacketListener;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptElementWaitForContact extends ScriptElement {
    private final int contactUid;
    private final long timeout;
    private final static long DEFAULT_TIMEOUT = 30000L; //30s
    private final Lock lock = new ReentrantLock();
    private final Condition condition;

    public ScriptElementWaitForContact(final int contactUid) {
        this(contactUid, DEFAULT_TIMEOUT);
    }

    public ScriptElementWaitForContact(final int contactUid, final long timeout) {
        this.contactUid = contactUid;
        this.timeout = timeout;
        condition = lock.newCondition();
    }

    @Override
    public void executeElement(final ScriptContext scriptContext) throws FrameworkException {

        final S88EventPacketListener s88EventPacketListener = new S88EventPacketListener(contactUid, S88EventPacketListener.ContactState.ACTIVATED) {
            @Override
            public void onSuccess() {
                lock.lock();
                try {
                    scriptContext.removePacketListener(this);
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
        }
    }
}
