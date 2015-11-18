package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.CS2CANCommands;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptElementSetDirection extends ScriptElement {
    private final int locId;
    private final int direction;

    public ScriptElementSetDirection(int locId, int direction) {
        this.locId = locId;
        this.direction = direction;
    }

    @Override
    public void executeElement(ScriptContext scriptContext) {
        scriptContext.writeCANPacket(
                CS2CANCommands.setDirection(locId, direction)
        );
    }
}
