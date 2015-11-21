package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.CANPacket;
import de.cortex42.maerklin.framework.CS2CANCommands;
import de.cortex42.maerklin.framework.PacketEvent;
import de.cortex42.maerklin.framework.PacketListener;

import java.util.function.BooleanSupplier;

/**
 * Created by ivo on 20.11.15.
 */
public class ScriptBooleanSupplierTrainVelocity implements BooleanSupplier {
    private final ScriptContext scriptContext;
    private final int locId;
    private final int velocity;
    private final long DELAY = 250L;

    public ScriptBooleanSupplierTrainVelocity(ScriptContext scriptContext, int locId, int velocity) {
        this.scriptContext = scriptContext;
        this.locId = locId;
        this.velocity = velocity;
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

                        if (canPacket.getCommand() == (CS2CANCommands.VELOCITY + 1)
                                && canPacket.getDlc() == CS2CANCommands.VELOCITY_SET_DLC) {
                            int velocity = (((canPacket.getData()[4] & 0xFF) << 8) | (canPacket.getData()[5] & 0xFF));

                            if (velocity == ScriptBooleanSupplierTrainVelocity.this.velocity) {
                                waitingThreadExchangeObject.value = true;
                                scriptContext.removePacketListener(this);
                            }
                        }
                    }
                });

                while (!waitingThreadExchangeObject.value) {
                    scriptContext.writeCANPacket(CS2CANCommands.queryVelocity(locId));

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
