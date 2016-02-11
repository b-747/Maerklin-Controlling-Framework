package bachelorarbeit.framework.scripting;

import bachelorarbeit.framework.CS2CANCommands;
import bachelorarbeit.framework.FrameworkException;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptElementSetDirection extends ScriptElement {
    public enum Direction {
        MAINTAIN,
        FORWARD,
        BACKWARD,
        TOGGLE,
    }

    private final int locId;
    private final Direction direction;

    public ScriptElementSetDirection(final int locId, final Direction direction) {
        this.locId = locId;
        this.direction = direction;
    }

    @Override
    public void executeElement(final ScriptContext scriptContext) throws FrameworkException {
        byte directionByte = 0;

        switch (direction) {
            case MAINTAIN:
                directionByte = CS2CANCommands.DIRECTION_MAINTAIN;
                break;

            case FORWARD:
                directionByte = CS2CANCommands.DIRECTION_FORWARD;
                break;

            case BACKWARD:
                directionByte = CS2CANCommands.DIRECTION_BACKWARD;
                break;

            case TOGGLE:
                directionByte = CS2CANCommands.DIRECTION_TOGGLE;
                break;
        }

        scriptContext.sendCANPacket(
                CS2CANCommands.setDirection(locId, directionByte)
        );
    }
}
