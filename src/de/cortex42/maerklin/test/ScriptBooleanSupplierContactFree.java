package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.CANPacket;
import de.cortex42.maerklin.framework.CS2CANCommands;
import de.cortex42.maerklin.framework.PacketEvent;
import de.cortex42.maerklin.framework.PacketListener;

import java.util.function.BooleanSupplier;

/**
 * Created by ivo on 20.11.15.
 */
public class ScriptBooleanSupplierContactFree implements BooleanSupplier {
    private final ScriptContext scriptContext;
    private final int contactId;
    private final long time;
    private final long DELAY = 250L;

    public ScriptBooleanSupplierContactFree(ScriptContext scriptContext, int contactId, long time) {
        this.scriptContext = scriptContext;
        this.contactId = contactId;
        this.time = time;
    }

    @Override
    public boolean getAsBoolean() {
        return check();
    }

    private boolean check() {
        final WaitingThreadExchangeObject waitingThreadExchangeObject = new WaitingThreadExchangeObject();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                scriptContext.addPacketListener(new PacketListener() {
                    @Override
                    public void packetEvent(PacketEvent packetEvent) {
                        CANPacket canPacket = packetEvent.getCANPacket();

                        if (canPacket.getCommand() == (CS2CANCommands.S88_EVENT + 1)
                                && canPacket.getDlc() == CS2CANCommands.S88_EVENT_RESPONSE_DLC
                                && canPacket.getID() == contactId
                                // && ((canPacket.getData()[4] & 0xFF) == CS2CANCommands.EQUIPMENT_POSITION_OFF) //old position
                                && ((canPacket.getData()[5] & 0xFF) == CS2CANCommands.EQUIPMENT_POSITION_OFF)
                                && (((canPacket.getData()[6] & 0xFF) << 8 | canPacket.getData()[7] & 0xFF) >= time)) {

                            waitingThreadExchangeObject.value = true;
                            scriptContext.removePacketListener(this);
                        }
                    }
                });

                while (!waitingThreadExchangeObject.value) {
                    scriptContext.writeCANPacket(CS2CANCommands.s88QueryStatus(contactId));

                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
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
