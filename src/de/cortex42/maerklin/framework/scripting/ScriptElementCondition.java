package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptElementCondition extends ScriptElement {
    private final BooleanEvent booleanEvent;
    private final ScriptElementCondition innerScriptElementCondition;
    private ScriptElementCondition otherScriptElementCondition = null;
    private boolean isOr = false;
    private boolean isAnd = false;
    private boolean isXor = false;

    public ScriptElementCondition(final ScriptElementCondition scriptElementCondition) {
        if (scriptElementCondition == null) {
            throw new IllegalArgumentException("scriptElementCondition must not be null.");
        }

        this.innerScriptElementCondition = scriptElementCondition;
        this.booleanEvent = null;
    }

    public ScriptElementCondition(final BooleanEvent booleanEvent) {
        if (booleanEvent == null) {
            throw new IllegalArgumentException("booleanEvent must not be null.");
        }

        this.booleanEvent = booleanEvent;
        this.innerScriptElementCondition = null;
    }

    private boolean checkCondition() throws FrameworkException {
        boolean returnValue;

        if (innerScriptElementCondition != null) {
            returnValue = innerScriptElementCondition.checkCondition();
        } else {
            returnValue = booleanEvent.getAsBoolean();
        }

        if (otherScriptElementCondition != null) {
            if (isOr) {
                returnValue = returnValue || otherScriptElementCondition.checkCondition();
            } else if (isAnd) {
                returnValue = returnValue && otherScriptElementCondition.checkCondition();
            } else {
                returnValue = returnValue ^ otherScriptElementCondition.checkCondition();
            }
        }

        return returnValue;
    }

    public ScriptElementCondition or(final ScriptElementCondition scriptElementCondition) throws ScriptConditionBooleanOperatorException {
        if (!(isOr || isAnd || isXor)) {
            otherScriptElementCondition = scriptElementCondition;
            isOr = true;

            return this;
        } else {
            throw new ScriptConditionBooleanOperatorException("Operator was already set.");
        }
    }

    public ScriptElementCondition and(final ScriptElementCondition scriptElementCondition) throws ScriptConditionBooleanOperatorException {
        if (!(isOr || isAnd || isXor)) {
            otherScriptElementCondition = scriptElementCondition;
            isAnd = true;

            return this;
        } else {
            throw new ScriptConditionBooleanOperatorException("Operator was already set.");
        }
    }

    public ScriptElementCondition xor(final ScriptElementCondition scriptElementCondition) throws ScriptConditionBooleanOperatorException {
        if (!(isOr || isAnd || isXor)) {
            otherScriptElementCondition = scriptElementCondition;
            isXor = true;

            return this;
        } else {
            throw new ScriptConditionBooleanOperatorException("Operator was already set.");
        }
    }

    @Override
    public void executeElement(final ScriptContext scriptContext) throws FrameworkException {
        if (!checkCondition()) {
            next = null; //condition not met -> do not execute the next element
        }
    }
}
