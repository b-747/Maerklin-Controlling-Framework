package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.CS2CANCommands;
import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 21.11.15.
 */
public class ScriptElementSetFunction extends ScriptElement {
    private final int locId;
    private final int function;
    private final int toggle;

    public ScriptElementSetFunction(final int locId, final int function, final int toggle) {
        this.locId = locId;
        this.function = function;
        this.toggle = toggle;
    }

    @Override
    public void executeElement(final ScriptContext scriptContext) throws FrameworkException {
        scriptContext.writeCANPacket(
                CS2CANCommands.toggleFunction(locId, function, toggle));
    }
}
