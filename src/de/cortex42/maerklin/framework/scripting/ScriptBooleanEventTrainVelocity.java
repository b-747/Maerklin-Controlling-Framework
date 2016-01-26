package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.CS2CANCommands;
import de.cortex42.maerklin.framework.FrameworkException;
import de.cortex42.maerklin.framework.packetlistener.VelocityPacketListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ivo on 20.11.15.
 */
public class ScriptBooleanEventTrainVelocity implements BooleanEvent {
    private final ScriptContext scriptContext;
    private final int locId;
    private final int velocity;
    private final long timeout;
    private final long DELAY = 250L;
    private final static long DEFAULT_TIMEOUT = 30000L; //30s
    private final Lock lock = new ReentrantLock();
    private final Condition condition;

    //timeout in ms
    public ScriptBooleanEventTrainVelocity(final ScriptContext scriptContext, final int locId, final int velocity, final long timeout) {
        this.scriptContext = scriptContext;
        this.locId = locId;
        this.velocity = velocity;
        this.timeout = timeout;
        condition = lock.newCondition();
    }

    public ScriptBooleanEventTrainVelocity(final ScriptContext scriptContext, final int locId, final int velocity) {
        this(scriptContext, locId, velocity, DEFAULT_TIMEOUT);
    }

    @Override
    public boolean getAsBoolean() throws FrameworkException {
        final ThreadExchangeObject threadExchangeObject = new ThreadExchangeObject();

        final VelocityPacketListener velocityPacketListener = new VelocityPacketListener() {
            @Override
            public void onSuccess() {
                if (getVelocity() == velocity) {
                    lock.lock();
                    try {
                        threadExchangeObject.value = true;
                        condition.signal();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        };
        scriptContext.addPacketListener(velocityPacketListener);

        final FrameworkException[] threadFrameworkException = new FrameworkException[1];
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        final ScheduledFuture[] scheduledFuture = new ScheduledFuture[1];
        scheduledFuture[0] = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    scriptContext.sendCANPacket(CS2CANCommands.queryVelocity(locId));
                } catch (final FrameworkException e) {
                    threadFrameworkException[0] = e;
                }
            }
        }, 0L, DELAY, TimeUnit.MILLISECONDS);

        lock.lock();
        try {
            if (!condition.await(timeout, TimeUnit.MILLISECONDS)) {
                //timeout
                return false;
            }
        } catch (final InterruptedException e) {
            if (threadFrameworkException[0] != null) {
                final FrameworkException[] frameworkExceptions = new FrameworkException[2];
                frameworkExceptions[0] = threadFrameworkException[0];
                frameworkExceptions[1] = new FrameworkException(e);
                throw new FrameworkException(frameworkExceptions);
            } else {
                throw new FrameworkException(e);
            }
        } finally {
            scriptContext.removePacketListener(velocityPacketListener);
            lock.unlock();
            scheduledFuture[0].cancel(true);
            scheduledExecutorService.shutdown();
        }

        if (threadFrameworkException[0] != null) {
            throw threadFrameworkException[0];
        }

        return threadExchangeObject.value;
    }
}
