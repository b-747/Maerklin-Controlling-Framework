package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.CS2CANCommands;
import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptElementSetDirection extends ScriptElement {
    private final int locId;
    private final int direction;

    public ScriptElementSetDirection(final int locId, final int direction) {
        this.locId = locId;
        this.direction = direction;
    }

    @Override
    public void executeElement(final ScriptContext scriptContext) throws FrameworkException {
        scriptContext.writeCANPacket(
                CS2CANCommands.setDirection(locId, direction)
        );
    }
}
