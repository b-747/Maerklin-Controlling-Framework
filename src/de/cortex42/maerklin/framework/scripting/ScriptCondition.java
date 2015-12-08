package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptCondition {
    private final BooleanEvent booleanEvent;

    public ScriptCondition(BooleanEvent booleanEvent) {
        this.booleanEvent = booleanEvent;
    }

    //todo add and, or, xor
    public boolean check() throws FrameworkException {
        return booleanEvent.getAsBoolean();
    }
}
