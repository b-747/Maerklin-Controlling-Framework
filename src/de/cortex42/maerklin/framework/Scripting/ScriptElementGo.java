package de.cortex42.maerklin.framework.Scripting;

import de.cortex42.maerklin.framework.CS2CANCommands;
import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptElementGo extends ScriptElement {
    @Override
    public void executeElement(ScriptContext scriptContext) throws FrameworkException {
        scriptContext.writeCANPacket(
                CS2CANCommands.go()
        );
    }
}
