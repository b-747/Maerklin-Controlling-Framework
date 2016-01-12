package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptCondition {
    private final BooleanEvent booleanEvent;
    private final ScriptCondition innerScriptCondition;
    private ScriptCondition otherScriptCondition = null;
    private boolean isOr = false;
    private boolean isAnd = false;
    private boolean isXor = false;

    public ScriptCondition(final ScriptCondition scriptCondition) { //todo check ALL parameters for null!
        if (scriptCondition == null) {
            throw new IllegalArgumentException("scriptCondition must not be null.");
        }

        this.innerScriptCondition = scriptCondition;
        this.booleanEvent = null;
    }

    public ScriptCondition(final BooleanEvent booleanEvent) {
        if (booleanEvent == null) {
            throw new IllegalArgumentException("booleanEvent must not be null.");
        }

        this.booleanEvent = booleanEvent;
        this.innerScriptCondition = null;
    }

    public boolean check() throws FrameworkException {
        boolean returnValue;

        if (innerScriptCondition != null) {
            returnValue = innerScriptCondition.check();
        } else {
            returnValue = booleanEvent.getAsBoolean();
        }

        if (otherScriptCondition != null) {
            if (isOr) {
                returnValue = returnValue || otherScriptCondition.check();
            } else if (isAnd) {
                returnValue = returnValue && otherScriptCondition.check();
            } else {
                returnValue = returnValue ^ otherScriptCondition.check();
            }
        }

        return returnValue;
    }

    public ScriptCondition or(final ScriptCondition scriptCondition) throws ScriptConditionBooleanOperatorException {
        if (!(isOr || isAnd || isXor)) {
            otherScriptCondition = scriptCondition;
            isOr = true;

            return this; //todo needed??
        } else {
            throw new ScriptConditionBooleanOperatorException("Operator was already set.");
        }
    }

    public ScriptCondition and(final ScriptCondition scriptCondition) throws ScriptConditionBooleanOperatorException {
        if (!(isOr || isAnd || isXor)) {
            otherScriptCondition = scriptCondition;
            isAnd = true;

            return this;
        } else {
            throw new ScriptConditionBooleanOperatorException("Operator was already set.");
        }
    }

    public ScriptCondition xor(final ScriptCondition scriptCondition) throws ScriptConditionBooleanOperatorException {
        if (!(isOr || isAnd || isXor)) {
            otherScriptCondition = scriptCondition;
            isXor = true;

            return this;
        } else {
            throw new ScriptConditionBooleanOperatorException("Operator was already set.");
        }
    }
}
