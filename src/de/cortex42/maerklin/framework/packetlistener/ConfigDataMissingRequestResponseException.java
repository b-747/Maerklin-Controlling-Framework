package de.cortex42.maerklin.framework.packetlistener;

/**
 * Created by ivo on 16.12.15.
 */
public class ConfigDataMissingRequestResponseException extends ConfigDataException {
    public ConfigDataMissingRequestResponseException() {
    }

    public ConfigDataMissingRequestResponseException(final String message) {
        super(message);
    }

    public ConfigDataMissingRequestResponseException(final Throwable cause) {
        super(cause);
    }

    public ConfigDataMissingRequestResponseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConfigDataMissingRequestResponseException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
