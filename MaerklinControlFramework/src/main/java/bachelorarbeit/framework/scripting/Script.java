package bachelorarbeit.framework.scripting;

import bachelorarbeit.framework.FrameworkException;

/**
 * Created by ivo on 13.11.15.
 */
public class Script {
    private final ScriptContext scriptContext;
    public ScriptElement first;

    public Script(final ScriptContext scriptContext) {
        if (scriptContext == null) {
            throw new IllegalArgumentException("scriptContext must not be null.");
        }
        this.scriptContext = scriptContext;
    }

    public void execute() throws FrameworkException {
        first.execute(scriptContext);
    }
}
