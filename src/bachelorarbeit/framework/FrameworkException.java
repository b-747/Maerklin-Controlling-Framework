package bachelorarbeit.framework;

/**
 * Created by ivo on 25.11.15.
 */
public class FrameworkException extends Exception {
    private final FrameworkException[] innerFrameworkExceptions;

    public FrameworkException() {
        innerFrameworkExceptions = null;
    }

    public FrameworkException(final String message) {
        super(message);
        innerFrameworkExceptions = null;
    }

    public FrameworkException(final Throwable cause) {
        super(cause);
        innerFrameworkExceptions = null;
    }

    public FrameworkException(final String message, final Throwable cause) {
        super(message, cause);
        innerFrameworkExceptions = null;
    }

    public FrameworkException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        innerFrameworkExceptions = null;
    }

    public FrameworkException(final FrameworkException[] innerFrameworkExceptions) {
        this.innerFrameworkExceptions = innerFrameworkExceptions;
    }

    public FrameworkException[] getInnerFrameworkExceptions() {
        return innerFrameworkExceptions;
    }
}
