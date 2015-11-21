package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.CANPacket;
import de.cortex42.maerklin.framework.CS2CANCommands;
import de.cortex42.maerklin.framework.PacketEvent;
import de.cortex42.maerklin.framework.PacketListener;

import java.util.function.BooleanSupplier;

/**
 * Created by ivo on 20.11.15.
 */
public class ScriptBooleanSupplierContactReached implements BooleanSupplier {
    private final ScriptContext scriptContext;
    private final int contactId;

    public ScriptBooleanSupplierContactReached(ScriptContext scriptContext, int contactId) {
        this.contactId = contactId;
        this.scriptContext = scriptContext;
    }

    @Override
    public boolean getAsBoolean() {
        return check();
    }

    private boolean check() {
        final WaitingThreadExchangeObject waitingThreadExchangeObject = new WaitingThreadExchangeObject();

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                scriptContext.addPacketListener(new PacketListener() {
                    @Override
                    public void packetEvent(PacketEvent packetEvent) {
                        CANPacket canPacket = packetEvent.getCANPacket();

                        if ((canPacket.getCommand() & 0xFE) == CS2CANCommands.S88_EVENT
                                && canPacket.getDlc() == CS2CANCommands.S88_EVENT_RESPONSE_DLC
                                && canPacket.getID() == contactId
                                && ((canPacket.getData()[5] & 0xFF) == CS2CANCommands.EQUIPMENT_POSITION_ON)) {
                            waitingThreadExchangeObject.value = true;
                            scriptContext.removePacketListener(this);
                        }
                    }
                });
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return waitingThreadExchangeObject.value;
    }
}
