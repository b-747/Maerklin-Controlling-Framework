package de.cortex42.maerklin.test;

/**
 * Created by ivo on 13.11.15.
 */
public class Script {
    private final ScriptContext scriptContext;
    public ScriptElement first;

    public Script(ScriptContext scriptContext) {
        this.scriptContext = scriptContext;
    }

    public void execute() {
        first.execute(scriptContext);
    }
}
