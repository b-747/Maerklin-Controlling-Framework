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

    public boolean check() {
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

    @Override
    public void executeElement(ScriptContext scriptContext) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!check()) {
                    //should be threadsafe (next is checked after this method has finished)
                    next = null; //stop further execution of the script
                }
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
