package bachelorarbeit.framework.packetlistener;

import bachelorarbeit.framework.FrameworkException;

/**
 * Created by ivo on 16.12.15.
 */
public class ConfigDataException extends FrameworkException {
    public ConfigDataException() {
    }

    public ConfigDataException(final String message) {
        super(message);
    }

    public ConfigDataException(final Throwable cause) {
        super(cause);
    }

    public ConfigDataException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConfigDataException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
