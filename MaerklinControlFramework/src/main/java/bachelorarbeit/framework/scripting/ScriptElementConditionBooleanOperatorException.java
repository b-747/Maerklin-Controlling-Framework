package bachelorarbeit.framework.scripting;

import bachelorarbeit.framework.FrameworkException;

/**
 * Created by ivo on 16.12.15.
 */
public class ScriptElementConditionBooleanOperatorException extends FrameworkException {
    public ScriptElementConditionBooleanOperatorException() {
    }

    public ScriptElementConditionBooleanOperatorException(final String message) {
        super(message);
    }

    public ScriptElementConditionBooleanOperatorException(final Throwable cause) {
        super(cause);
    }

    public ScriptElementConditionBooleanOperatorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ScriptElementConditionBooleanOperatorException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
