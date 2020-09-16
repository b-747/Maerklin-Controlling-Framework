package bachelorarbeit.framework.packetlistener;

import bachelorarbeit.framework.CANPacket;
import bachelorarbeit.framework.CS2CANCommands;
import bachelorarbeit.framework.FrameworkException;

/**
 * Created by ivo on 19.12.15.
 */
public abstract class DirectionPacketListener implements PacketListener {
    public enum Direction {
        MAINTAIN,
        FORWARD,
        BACKWARD,
        TOGGLE,
        UNKNOWN
    }

    private Direction direction;

    @Override
    public void onPacketEvent(final PacketEvent packetEvent) {
        final CANPacket canPacket = packetEvent.getCANPacket();

        if ((canPacket.getCommand() & 0xFE) == CS2CANCommands.DIRECTION
                && (canPacket.getDlc() == CS2CANCommands.DIRECTION_SET_DLC)) {
            final byte directionByte = canPacket.getData()[4];

            switch (directionByte) {
                case CS2CANCommands.DIRECTION_MAINTAIN:
                    direction = Direction.MAINTAIN;
                    break;

                case CS2CANCommands.DIRECTION_FORWARD:
                    direction = Direction.FORWARD;
                    break;

                case CS2CANCommands.DIRECTION_BACKWARD:
                    direction = Direction.BACKWARD;
                    break;

                case CS2CANCommands.DIRECTION_TOGGLE:
                    direction = Direction.TOGGLE;
                    break;

                default:
                    direction = Direction.UNKNOWN;
                    break;
            }

            onSuccess();
        }
    }

    @Override
    public void onException(final FrameworkException frameworkException) {
        //never happens
    }

    @Override
    public abstract void onSuccess();

    public Direction getDirection() {
        return direction;
    }
}
