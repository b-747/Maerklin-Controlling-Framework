package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 22.11.15.
 */
public interface BooleanEvent {
    boolean getAsBoolean() throws FrameworkException;
}
