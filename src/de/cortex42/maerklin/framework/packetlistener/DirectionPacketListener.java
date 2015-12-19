package de.cortex42.maerklin.framework.packetlistener;

import de.cortex42.maerklin.framework.CANPacket;
import de.cortex42.maerklin.framework.CS2CANCommands;
import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 19.12.15.
 */
public abstract class DirectionPacketListener extends PacketListener {
    public enum DIRECTION {
        MAINTAIN,
        FORWARD,
        BACKWARD,
        TOGGLE,
        UNKNOWN
    }

    private DIRECTION direction;

    @Override
    public void onPacketEvent(final PacketEvent packetEvent) {
        CANPacket canPacket = packetEvent.getCANPacket();

        if ((canPacket.getCommand() & 0xFE) == CS2CANCommands.DIRECTION
                && (canPacket.getDlc() == CS2CANCommands.DIRECTION_SET_DLC)) {
            byte[] data = canPacket.getData();

            byte directionByte = canPacket.getData()[4];

            switch (directionByte) {
                case CS2CANCommands.DIRECTION_MAINTAIN:
                    direction = DIRECTION.MAINTAIN;
                    break;

                case CS2CANCommands.DIRECTION_FORWARD:
                    direction = DIRECTION.FORWARD;
                    break;

                case CS2CANCommands.DIRECTION_BACKWARD:
                    direction = DIRECTION.BACKWARD;
                    break;

                case CS2CANCommands.DIRECTION_TOGGLE:
                    direction = DIRECTION.TOGGLE;
                    break;

                default:
                    direction = DIRECTION.UNKNOWN;
                    break;
            }

            onSuccess();
        }
    }

    @Override
    public void onException(final FrameworkException frameworkException) {
        //never happens
    }

    public DIRECTION getDirection() {
        return direction;
    }
}
