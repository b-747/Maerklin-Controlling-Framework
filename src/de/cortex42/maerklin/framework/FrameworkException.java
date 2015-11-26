package de.cortex42.maerklin.framework;

/**
 * Created by ivo on 25.11.15.
 */
public class FrameworkException extends Exception {
    private final FrameworkException[] innerFrameworkExceptions;

    public FrameworkException() {
        innerFrameworkExceptions = null;
    }

    public FrameworkException(String message) {
        super(message);
        innerFrameworkExceptions = null;
    }

    public FrameworkException(Throwable cause) {
        super(cause);
        innerFrameworkExceptions = null;
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
        innerFrameworkExceptions = null;
    }

    public FrameworkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        innerFrameworkExceptions = null;
    }

    public FrameworkException(FrameworkException[] innerFrameworkExceptions) {
        this.innerFrameworkExceptions = innerFrameworkExceptions;
    }

    public FrameworkException[] getInnerFrameworkExceptions() {
        return innerFrameworkExceptions;
    }
}
