package de.cortex42.maerklin.framework.Scripting;

import de.cortex42.maerklin.framework.*;

/**
 * Created by ivo on 20.11.15.
 */
public class ScriptBooleanSupplierContactFree implements BooleanEvent {
    private final ScriptContext scriptContext;
    private final int contactId;
    private final long freeTime;

    public ScriptBooleanSupplierContactFree(ScriptContext scriptContext, int contactId, long freeTime) {
        this.scriptContext = scriptContext;
        this.contactId = contactId;
        this.freeTime = freeTime;
    }

    @Override
    public boolean getAsBoolean() throws FrameworkException {
        return check();
    }

    private boolean check() throws FrameworkException {
        final WaitingThreadExchangeObject waitingThreadExchangeObject = new WaitingThreadExchangeObject();
        final boolean[] isContactFree = {false};

        scriptContext.addPacketListener(new PacketListener() {
            @Override
            public void packetEvent(PacketEvent packetEvent) {
                CANPacket canPacket = packetEvent.getCANPacket();

                if ((canPacket.getCommand() & 0xFE) == CS2CANCommands.S88_EVENT
                        && canPacket.getDlc() == CS2CANCommands.S88_EVENT_RESPONSE_DLC
                        && canPacket.getID() == contactId) {
                    isContactFree[0] = (canPacket.getData()[5] & 0xFF) == CS2CANCommands.EQUIPMENT_POSITION_OFF;
                    waitingThreadExchangeObject.value = true;
                    scriptContext.removePacketListener(this);
                }
            }
        });

        scriptContext.writeCANPacket(CS2CANCommands.s88QueryStatus(contactId)); //query contact status
        //if free => wait => if no s88 event => return true
        //if not free => return false
        while (!waitingThreadExchangeObject.value) { //wait for query response
            synchronized (waitingThreadExchangeObject) {
                try {
                    waitingThreadExchangeObject.wait();
                } catch (InterruptedException e) {
                    throw new FrameworkException(e);
                }
            }
        }

        if (isContactFree[0]) {
            //contact is free

            waitingThreadExchangeObject.value = false; //reset and add another listener

            PacketListener packetListener = new PacketListener() {
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
            }

            scriptContext.removePacketListener(packetListener);

            //if no S88 event occured until now (value is false), then the contact remained free
            return !waitingThreadExchangeObject.value;

        } else {
            return false;
        }

    }
}
