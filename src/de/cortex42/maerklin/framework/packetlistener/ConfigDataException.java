package de.cortex42.maerklin.framework.packetlistener;

import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 16.12.15.
 */
public class ConfigDataException extends FrameworkException {
    public ConfigDataException() {
    }

    public ConfigDataException(String message) {
        super(message);
    }

    public ConfigDataException(Throwable cause) {
        super(cause);
    }

    public ConfigDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
