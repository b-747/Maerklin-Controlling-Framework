package de.cortex42.maerklin.test;

/**
 * Created by ivo on 13.11.15.
 */
public abstract class ScriptElement {
    public ScriptElement next;

    public void execute(ScriptContext scriptContext) {
        executeElement(scriptContext); //implemented by subclass

        if (next != null) {
            next.execute(scriptContext);
        }
    }

    public abstract void executeElement(ScriptContext scriptContext);
}
