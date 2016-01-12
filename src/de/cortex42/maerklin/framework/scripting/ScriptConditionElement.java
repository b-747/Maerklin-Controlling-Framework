package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptConditionElement extends ScriptElement {
    private final BooleanEvent booleanEvent;
    private final ScriptConditionElement innerScriptConditionElement;
    private ScriptConditionElement otherScriptConditionElement = null;
    private boolean isOr = false;
    private boolean isAnd = false;
    private boolean isXor = false;

    public ScriptConditionElement(final ScriptConditionElement scriptConditionElement) { //todo check ALL parameters for null!
        if (scriptConditionElement == null) {
            throw new IllegalArgumentException("scriptConditionElement must not be null.");
        }

        this.innerScriptConditionElement = scriptConditionElement;
        this.booleanEvent = null;
    }

    public ScriptConditionElement(final BooleanEvent booleanEvent) {
        if (booleanEvent == null) {
            throw new IllegalArgumentException("booleanEvent must not be null.");
        }

        this.booleanEvent = booleanEvent;
        this.innerScriptConditionElement = null;
    }

    private boolean checkCondition() throws FrameworkException {
        boolean returnValue;

        if (innerScriptConditionElement != null) {
            returnValue = innerScriptConditionElement.checkCondition();
        } else {
            returnValue = booleanEvent.getAsBoolean();
        }

        if (otherScriptConditionElement != null) {
            if (isOr) {
                returnValue = returnValue || otherScriptConditionElement.checkCondition();
            } else if (isAnd) {
                returnValue = returnValue && otherScriptConditionElement.checkCondition();
            } else {
                returnValue = returnValue ^ otherScriptConditionElement.checkCondition();
            }
        }

        return returnValue;
    }

    public ScriptConditionElement or(final ScriptConditionElement scriptConditionElement) throws ScriptConditionBooleanOperatorException {
        if (!(isOr || isAnd || isXor)) {
            otherScriptConditionElement = scriptConditionElement;
            isOr = true;

            return this;
        } else {
            throw new ScriptConditionBooleanOperatorException("Operator was already set.");
        }
    }

    public ScriptConditionElement and(final ScriptConditionElement scriptConditionElement) throws ScriptConditionBooleanOperatorException {
        if (!(isOr || isAnd || isXor)) {
            otherScriptConditionElement = scriptConditionElement;
            isAnd = true;

            return this;
        } else {
            throw new ScriptConditionBooleanOperatorException("Operator was already set.");
        }
    }

    public ScriptConditionElement xor(final ScriptConditionElement scriptConditionElement) throws ScriptConditionBooleanOperatorException {
        if (!(isOr || isAnd || isXor)) {
            otherScriptConditionElement = scriptConditionElement;
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
