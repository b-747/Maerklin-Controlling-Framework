package de.cortex42.maerklin.framework.Scripting;

import de.cortex42.maerklin.framework.*;

/**
 * Created by ivo on 20.11.15.
 */
public class ScriptBooleanSupplierTrainVelocity implements BooleanEvent {
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
    public boolean getAsBoolean() throws FrameworkException {
        return check();
    }

    private boolean check() throws FrameworkException {
        final WaitingThreadExchangeObject waitingThreadExchangeObject = new WaitingThreadExchangeObject();

        scriptContext.addPacketListener(new PacketListener() {
            @Override
            public void packetEvent(PacketEvent packetEvent) {
                CANPacket canPacket = packetEvent.getCANPacket();

                if (canPacket.getCommand() == (CS2CANCommands.VELOCITY + 1)
                        && canPacket.getDlc() == CS2CANCommands.VELOCITY_SET_DLC) {
                    int velocityValue = (((canPacket.getData()[4] & 0xFF) << 8) | (canPacket.getData()[5] & 0xFF));

                    if (velocityValue == velocity) {
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
                throw new FrameworkException(e);
            }
        }

        return waitingThreadExchangeObject.value;
    }
}
