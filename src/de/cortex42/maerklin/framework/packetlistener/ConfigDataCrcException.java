package de.cortex42.maerklin.framework.packetlistener;

/**
 * Created by ivo on 16.12.15.
 */
public class ConfigDataCrcException extends ConfigDataException {
    public ConfigDataCrcException() {
    }

    public ConfigDataCrcException(String message) {
        super(message);
    }

    public ConfigDataCrcException(Throwable cause) {
        super(cause);
    }

    public ConfigDataCrcException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigDataCrcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
