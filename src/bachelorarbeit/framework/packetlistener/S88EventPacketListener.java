package bachelorarbeit.framework.packetlistener;

import bachelorarbeit.framework.CANPacket;
import bachelorarbeit.framework.CS2CANCommands;
import bachelorarbeit.framework.FrameworkException;

/**
 * Created by ivo on 17.12.15.
 */
public abstract class S88EventPacketListener implements PacketListener {
    public enum ContactState {
        ACTIVATED,
        DEACTIVATED,
        IRRELEVANT
    }

    private final int contactUid;
    private final ContactState contactState;

    public S88EventPacketListener(final int contactUid, final ContactState contactState) {
        this.contactUid = contactUid;
        this.contactState = contactState;
    }

    @Override
    public void onPacketEvent(final PacketEvent packetEvent) {
        final CANPacket canPacket = packetEvent.getCANPacket();

        if ((canPacket.getCommand() & 0xFE) == CS2CANCommands.S88_EVENT
                && canPacket.getDlc() == CS2CANCommands.S88_EVENT_RESPONSE_DLC
                && canPacket.getUid() == contactUid
                && (contactState == ContactState.IRRELEVANT || (canPacket.getData()[5] == (contactState == ContactState.ACTIVATED ? CS2CANCommands.CONTACT_ACTIVATED : CS2CANCommands.CONTACT_DEACTIVATED)))) {
            onSuccess();
        }
    }

    @Override
    public void onException(final FrameworkException frameworkException) {
        //never happens
    }

    @Override
    public abstract void onSuccess();
}
