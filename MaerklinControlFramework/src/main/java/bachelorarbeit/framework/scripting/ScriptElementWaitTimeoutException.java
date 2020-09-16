package bachelorarbeit.framework.scripting;

import bachelorarbeit.framework.FrameworkException;

/**
 * Created by ivo on 17.12.15.
 */
public class ScriptElementWaitTimeoutException extends FrameworkException {
    public ScriptElementWaitTimeoutException() {
    }

    public ScriptElementWaitTimeoutException(final String message) {
        super(message);
    }

    public ScriptElementWaitTimeoutException(final Throwable cause) {
        super(cause);
    }

    public ScriptElementWaitTimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ScriptElementWaitTimeoutException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
