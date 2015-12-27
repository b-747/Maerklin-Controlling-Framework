package de.cortex42.maerklin.framework.packetlistener;

/**
 * Created by ivo on 27.12.15.
 */
public class ConfigDataDecompressException extends ConfigDataException {
    public ConfigDataDecompressException() {
    }

    public ConfigDataDecompressException(final String message) {
        super(message);
    }

    public ConfigDataDecompressException(final Throwable cause) {
        super(cause);
    }

    public ConfigDataDecompressException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConfigDataDecompressException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
