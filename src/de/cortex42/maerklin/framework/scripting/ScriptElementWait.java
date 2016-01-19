package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 13.11.15.
 */
public class ScriptElementWait extends ScriptElement {
    private final long milliseconds;

    public ScriptElementWait(final long milliseconds) {
        if (milliseconds < 0L) {
            throw new IllegalArgumentException("milliseconds must be positive.");
        }
        this.milliseconds = milliseconds;
    }

    @Override
    public void executeElement(final ScriptContext scriptContext) throws FrameworkException {
        try {
            Thread.sleep(milliseconds);
        } catch (final InterruptedException e) {
            throw new FrameworkException(e);
        }
    }
}
