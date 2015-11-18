package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.CS2CANCommands;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptElementStop extends ScriptElement {
    @Override
    public void executeElement(ScriptContext scriptContext) {
        scriptContext.writeCANPacket(
                CS2CANCommands.stop()
        );
    }
}
