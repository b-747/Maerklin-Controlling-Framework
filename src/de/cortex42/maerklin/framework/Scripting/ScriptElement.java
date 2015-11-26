package de.cortex42.maerklin.framework.Scripting;

import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 13.11.15.
 */
public abstract class ScriptElement {
    public ScriptElement next;

    public void execute(ScriptContext scriptContext) throws FrameworkException {
        executeElement(scriptContext); //implemented by subclass

        if (next != null) {
            next.execute(scriptContext);
        }
    }

    public abstract void executeElement(ScriptContext scriptContext) throws FrameworkException;
}
