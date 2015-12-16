package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 16.12.15.
 */
public class ScriptConditionBooleanOperatorException extends FrameworkException {
    public ScriptConditionBooleanOperatorException() {
    }

    public ScriptConditionBooleanOperatorException(String message) {
        super(message);
    }

    public ScriptConditionBooleanOperatorException(Throwable cause) {
        super(cause);
    }

    public ScriptConditionBooleanOperatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptConditionBooleanOperatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
