package de.cortex42.maerklin.framework;

/**
 * Created by ivo on 16.12.15.
 */
public class SerialPortException extends FrameworkException {
    public SerialPortException() {
    }

    public SerialPortException(String message) {
        super(message);
    }

    public SerialPortException(Throwable cause) {
        super(cause);
    }

    public SerialPortException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerialPortException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
