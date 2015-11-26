package de.cortex42.maerklin.framework.Scripting;

import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptElementConditionChecker extends ScriptElement {
    private final ScriptCondition scriptCondition;
    private ScriptElementConditionChecker otherScriptElementConditionChecker;
    private boolean isOr = false;
    private boolean isAnd = false;
    private boolean isXor = false;
    private boolean conditionMet = false;

    public ScriptElementConditionChecker(ScriptCondition scriptCondition) {
        this.scriptCondition = scriptCondition;
    }

    public void or(ScriptElementConditionChecker scriptElementConditionChecker) throws FrameworkException {
        if (!(isOr || isAnd || isXor)) {
            otherScriptElementConditionChecker = scriptElementConditionChecker;
            isOr = true;
        } else {
            throw new FrameworkException("Operation was already set.");
        }
    }

    public void and(ScriptElementConditionChecker scriptElementConditionChecker) throws FrameworkException {
        if (!(isOr || isAnd || isXor)) {
            otherScriptElementConditionChecker = scriptElementConditionChecker;
            isAnd = true;
        } else {
            throw new FrameworkException("Operation was already set.");
        }
    }

    public void xor(ScriptElementConditionChecker scriptElementConditionChecker) throws FrameworkException {
        if (!(isOr || isAnd || isXor)) {
            otherScriptElementConditionChecker = scriptElementConditionChecker;
            isXor = true;
        } else {
            throw new FrameworkException("Operation was already set.");
        }
    }

    private boolean check() throws FrameworkException {
        if (otherScriptElementConditionChecker != null) {
            if (isOr) {
                return scriptCondition.check() || otherScriptElementConditionChecker.check();
            } else if (isAnd) {
                return scriptCondition.check() && otherScriptElementConditionChecker.check();
            } else if (isXor) {
                return scriptCondition.check() ^ otherScriptElementConditionChecker.check();
            } else {
                throw new FrameworkException("No operation was set.");
            }
        }

        return scriptCondition.check();
    }

    public boolean isConditionMet() {
        return conditionMet;
    }

    @Override
    public void executeElement(ScriptContext scriptContext) throws FrameworkException {
        conditionMet = check();

        if (!conditionMet) {
            next = null; //condition not met -> do not execute the next element
        }
    }
}
