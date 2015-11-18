package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.CS2CANCommands;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptElementSetVelocity extends ScriptElement {
    private final int locId;
    private final int velocity;

    public ScriptElementSetVelocity(int locId, int velocity) {
        this.locId = locId;
        this.velocity = velocity;
    }

    @Override
    public void executeElement(ScriptContext scriptContext) {
        scriptContext.writeCANPacket(
                CS2CANCommands.setVelocity(locId, velocity)
        );
    }
}
