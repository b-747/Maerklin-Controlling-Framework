package de.cortex42.maerklin.test;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptConditionChecker extends ScriptElement {
    private final ScriptCondition scriptCondition;
    private ScriptConditionChecker otherScriptConditionChecker;
    private boolean isOr = false;
    private boolean isAnd = false;
    private boolean isXor = false;
    private boolean conditionMet = false;

    public ScriptConditionChecker(ScriptCondition scriptCondition) {
        this.scriptCondition = scriptCondition;
    }

    public void or(ScriptConditionChecker scriptConditionChecker) {
        if (!(isOr || isAnd || isXor)) {
            otherScriptConditionChecker = scriptConditionChecker;
            isOr = true;
        } else {
            throw new RuntimeException("Operation was already set.");
        }
    }

    public void and(ScriptConditionChecker scriptConditionChecker) {
        if (!(isOr || isAnd || isXor)) {
            otherScriptConditionChecker = scriptConditionChecker;
            isAnd = true;
        } else {
            throw new RuntimeException("Operation was already set.");
        }
    }

    public void xor(ScriptConditionChecker scriptConditionChecker) {
        if (!(isOr || isAnd || isXor)) {
            otherScriptConditionChecker = scriptConditionChecker;
            isXor = true;
        } else {
            throw new RuntimeException("Operation was already set.");
        }
    }

    private boolean check() {
        if (otherScriptConditionChecker != null) {
            if (isOr) {
                return scriptCondition.check() || otherScriptConditionChecker.check();
            } else if (isAnd) {
                return scriptCondition.check() && otherScriptConditionChecker.check();
            } else if (isXor) {
                return scriptCondition.check() ^ otherScriptConditionChecker.check();
            } else {
                throw new RuntimeException("No operation was set.");
            }
        }

        return scriptCondition.check();
    }

    public boolean isConditionMet() {
        return conditionMet;
    }

    @Override
    public void executeElement(ScriptContext scriptContext) {
        conditionMet = check();

        if (!conditionMet) {
            next = null; //stop further execution of the script
        }
    }
}
