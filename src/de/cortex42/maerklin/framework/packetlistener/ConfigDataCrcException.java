package de.cortex42.maerklin.framework.packetlistener;

/**
 * Created by ivo on 16.12.15.
 */
public class ConfigDataCrcException extends ConfigDataException {
    public ConfigDataCrcException() {
    }

    public ConfigDataCrcException(final String message) {
        super(message);
    }

    public ConfigDataCrcException(final Throwable cause) {
        super(cause);
    }

    public ConfigDataCrcException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConfigDataCrcException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
