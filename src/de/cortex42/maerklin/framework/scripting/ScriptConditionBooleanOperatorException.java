package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 16.12.15.
 */
public class ScriptConditionBooleanOperatorException extends FrameworkException {
    public ScriptConditionBooleanOperatorException() {
    }

    public ScriptConditionBooleanOperatorException(final String message) {
        super(message);
    }

    public ScriptConditionBooleanOperatorException(final Throwable cause) {
        super(cause);
    }

    public ScriptConditionBooleanOperatorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ScriptConditionBooleanOperatorException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
