package de.cortex42.maerklin.test;

/**
 * Created by ivo on 13.11.15.
 */
public class Script {
    public ScriptElement first;

    public void execute(ScriptContext scriptContext) {
        first.execute(scriptContext);
    }
}
