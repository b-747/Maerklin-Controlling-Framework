package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptElementConditionChecker extends ScriptElement {
    private final ScriptCondition scriptCondition;
    private boolean conditionMet = false;

    public ScriptElementConditionChecker(ScriptCondition scriptCondition) {
        this.scriptCondition = scriptCondition;
    }

    private boolean check() throws FrameworkException {
        return scriptCondition.check();
    }

    @Override
    public void executeElement(ScriptContext scriptContext) throws FrameworkException {
        conditionMet = check();

        if (!conditionMet) {
            next = null; //condition not met -> do not execute the next element
        }
    }
}
