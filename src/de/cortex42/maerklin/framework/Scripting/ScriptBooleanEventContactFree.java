package de.cortex42.maerklin.framework.Scripting;

import de.cortex42.maerklin.framework.*;

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
    private final static long DEFAULT_TIMEOUT = 60000; //60s
    private final Lock lock = new ReentrantLock();
    private final Condition condition;

    public ScriptBooleanEventContactFree(ScriptContext scriptContext, int contactId, long freeTime, long timeout) {
        this.scriptContext = scriptContext;
        this.contactId = contactId;
        this.freeTime = freeTime;
        this.timeout = timeout;
        condition = lock.newCondition();
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

        PacketListener packetListener = new PacketListener() {
            @Override
            public void packetEvent(PacketEvent packetEvent) {
                CANPacket canPacket = packetEvent.getCANPacket();

                if ((canPacket.getCommand() & 0xFE) == CS2CANCommands.S88_EVENT
                        && canPacket.getDlc() == CS2CANCommands.S88_EVENT_RESPONSE_DLC
                        && canPacket.getID() == contactId
                        && (canPacket.getData()[5] & 0xFF) == CS2CANCommands.EQUIPMENT_POSITION_OFF) {

                    lock.lock();
                    waitingThreadExchangeObject.value = true;
                    condition.signal();
                    lock.unlock();
                }
            }
        };

        scriptContext.addPacketListener(packetListener);

        //wait until contact is free
        lock.lock();
        try{
            if(!condition.await(timeout, TimeUnit.MILLISECONDS)){
                //timeout
                return false;
            }
        }catch(InterruptedException e){
            throw new FrameworkException(e);
        }finally {
            lock.unlock();
            scriptContext.removePacketListener(packetListener);
        }

        waitingThreadExchangeObject.value = false; //reset and add another listener

        packetListener = new PacketListener() {
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
        }finally {
            scriptContext.removePacketListener(packetListener);
        }

        //if no S88 event occured until now (value is false), then the contact remained free
        return !waitingThreadExchangeObject.value;
    }
}
