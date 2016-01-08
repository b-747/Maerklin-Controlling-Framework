package de.cortex42.maerklin.framework.packetlistener;

import de.cortex42.maerklin.framework.CANPacket;
import de.cortex42.maerklin.framework.CS2CANCommands;
import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 17.12.15.
 */
public abstract class S88EventPacketListener implements PacketListener {
    private final int contactId;
    private final boolean positionOn;
    private final boolean positionDoesNotMatter;

    public S88EventPacketListener(final int contactId, final boolean positionOn) {
        this.contactId = contactId;
        this.positionOn = positionOn;
        this.positionDoesNotMatter = false;
    }

    public S88EventPacketListener(final int contactId) {
        this.contactId = contactId;
        this.positionOn = false;
        this.positionDoesNotMatter = true;
    }

    @Override
    public void onPacketEvent(final PacketEvent packetEvent) {
        final CANPacket canPacket = packetEvent.getCANPacket();

        if ((canPacket.getCommand() & 0xFE) == CS2CANCommands.S88_EVENT
                && canPacket.getDlc() == CS2CANCommands.S88_EVENT_RESPONSE_DLC
                && canPacket.getUid() == contactId
                && (positionDoesNotMatter || (canPacket.getData()[5] == (positionOn ? CS2CANCommands.EQUIPMENT_POSITION_ON : CS2CANCommands.EQUIPMENT_POSITION_OFF)))) {
            onSuccess();
        }
    }

    @Override
    public void onException(final FrameworkException frameworkException) {
        //never happens
    }
}
