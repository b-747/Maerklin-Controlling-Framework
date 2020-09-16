package bachelorarbeit.framework.scripting;

import bachelorarbeit.framework.CS2CANCommands;
import bachelorarbeit.framework.FrameworkException;

/**
 * Created by ivo on 21.11.15.
 */
public class ScriptElementSetFunction extends ScriptElement {
    public enum ToggleState {
        ON,
        OFF
    }

    private final int trainUid;
    private final int function;
    private final ToggleState toggleState;

    public ScriptElementSetFunction(final int trainUid, final int function, final ToggleState toggleState) {
        this.trainUid = trainUid;
        this.function = function;
        this.toggleState = toggleState;
    }

    @Override
    public void executeElement(final ScriptContext scriptContext) throws FrameworkException {
        scriptContext.sendCANPacket(
                CS2CANCommands.toggleFunction(trainUid, function, toggleState == ToggleState.ON ? CS2CANCommands.FUNCTION_ON : CS2CANCommands.FUNCTION_OFF));
    }
}
