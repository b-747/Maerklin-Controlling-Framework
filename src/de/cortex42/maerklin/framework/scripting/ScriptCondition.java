package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptCondition {
    private final BooleanEvent booleanEvent;
    private ScriptCondition otherScriptCondition = null;
    private boolean isOr = false;
    private boolean isAnd = false;
    private boolean isXor = false;

    public ScriptCondition(BooleanEvent booleanEvent) {
        this.booleanEvent = booleanEvent;
    }

    public boolean check() throws FrameworkException {
        if (otherScriptCondition != null) {
            if (isOr) {
                return booleanEvent.getAsBoolean() || otherScriptCondition.check();
            } else if (isAnd) {
                return booleanEvent.getAsBoolean() && otherScriptCondition.check();
            } else if (isXor) {
                return booleanEvent.getAsBoolean() ^ otherScriptCondition.check();
            } else {
                throw new FrameworkException("No operation was set.");
            }
        }

        return booleanEvent.getAsBoolean();
    }

    public ScriptCondition or(ScriptCondition scriptCondition) throws FrameworkException {
        if (!(isOr || isAnd || isXor)) {
            otherScriptCondition = scriptCondition;
            isOr = true;

            return this;
        } else {
            throw new FrameworkException("Operation was already set.");
        }
    }

    public ScriptCondition and(ScriptCondition scriptCondition) throws FrameworkException {
        if (!(isOr || isAnd || isXor)) {
            otherScriptCondition = scriptCondition;
            isAnd = true;

            return this;
        } else {
            throw new FrameworkException("Operation was already set.");
        }
    }

    public ScriptCondition xor(ScriptCondition scriptCondition) throws FrameworkException {
        if (!(isOr || isAnd || isXor)) {
            otherScriptCondition = scriptCondition;
            isXor = true;

            return this;
        } else {
            throw new FrameworkException("Operation was already set.");
        }
    }
}
