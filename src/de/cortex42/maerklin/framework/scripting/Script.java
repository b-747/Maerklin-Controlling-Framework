package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 13.11.15.
 */
public class Script {
    private final ScriptContext scriptContext;
    public ScriptElement first;

    public Script(final ScriptContext scriptContext) { //todo check for null
        this.scriptContext = scriptContext;
    }

    public void execute() throws FrameworkException {
        first.execute(scriptContext);
    }
}
