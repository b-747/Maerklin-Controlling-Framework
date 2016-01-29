package bachelorarbeit.framework.scripting;

import bachelorarbeit.framework.CS2CANCommands;
import bachelorarbeit.framework.FrameworkException;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptElementSetVelocity extends ScriptElement {
    private final int locId;
    private final int velocity;

    public ScriptElementSetVelocity(final int locId, final int velocity) {
        this.locId = locId;
        this.velocity = velocity;
    }

    @Override
    public void executeElement(final ScriptContext scriptContext) throws FrameworkException {
        scriptContext.sendCANPacket(
                CS2CANCommands.setVelocity(locId, velocity)
        );
    }
}
