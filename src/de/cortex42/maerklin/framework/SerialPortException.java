package de.cortex42.maerklin.framework;

/**
 * Created by ivo on 16.12.15.
 */
public class SerialPortException extends FrameworkException {
    public SerialPortException() {
    }

    public SerialPortException(final String message) {
        super(message);
    }

    public SerialPortException(final Throwable cause) {
        super(cause);
    }

    public SerialPortException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SerialPortException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
