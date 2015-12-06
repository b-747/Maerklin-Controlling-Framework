package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.*;

/**
 * Created by ivo on 20.11.15.
 */
public class ScriptBooleanEventTrainVelocity implements BooleanEvent {
    private final ScriptContext scriptContext;
    private final int locId;
    private final int velocity;
    private final long timeout;
    private final long DELAY = 250L;
    private final static long DEFAULT_TIMEOUT = 60000L; //60s

    public ScriptBooleanEventTrainVelocity(ScriptContext scriptContext, int locId, int velocity, long timeout) {
        this.scriptContext = scriptContext;
        this.locId = locId;
        this.velocity = velocity;
        this.timeout = timeout;
    }

    public ScriptBooleanEventTrainVelocity(ScriptContext scriptContext, int locId, int velocity) {
        this(scriptContext, locId, velocity, DEFAULT_TIMEOUT);
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

                if ((canPacket.getCommand() & 0xFE) == CS2CANCommands.VELOCITY
                        && canPacket.getDlc() == CS2CANCommands.VELOCITY_SET_DLC) {
                    int velocityValue = (((canPacket.getData()[4] & 0xFF) << 8) | (canPacket.getData()[5] & 0xFF));

                    if (velocityValue == velocity) {
                        waitingThreadExchangeObject.value = true;
                    }
                }
            }
        };

        scriptContext.addPacketListener(packetListener);

        long counter = 0L;
        while (!waitingThreadExchangeObject.value) {
            scriptContext.writeCANPacket(CS2CANCommands.queryVelocity(locId));

            try {
                Thread.sleep(DELAY);
                counter += DELAY;

                if (counter >= timeout) {
                    //timeout
                    return false;
                }
            } catch (InterruptedException e) {
                throw new FrameworkException(e);
            } finally {
                scriptContext.removePacketListener(packetListener);
            }
        }

        return waitingThreadExchangeObject.value;
    }
}
