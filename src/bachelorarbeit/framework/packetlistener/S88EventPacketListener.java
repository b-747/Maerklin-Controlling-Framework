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

    private final int contactId;
    private final ContactState contactState;

    //todo rename Id to Uid
    public S88EventPacketListener(final int contactId, final ContactState contactState) {
        this.contactId = contactId;
        this.contactState = contactState;
    }

    @Override
    public void onPacketEvent(final PacketEvent packetEvent) {
        final CANPacket canPacket = packetEvent.getCANPacket();

        if ((canPacket.getCommand() & 0xFE) == CS2CANCommands.S88_EVENT
                && canPacket.getDlc() == CS2CANCommands.S88_EVENT_RESPONSE_DLC
                && canPacket.getUid() == contactId
                && (contactState == ContactState.IRRELEVANT || (canPacket.getData()[5] == (contactState == ContactState.ACTIVATED ? CS2CANCommands.CONTACT_ACTIVATED : CS2CANCommands.CONTACT_DEACTIVATED)))) {
            onSuccess();
        }
    }

    @Override
    public void onException(final FrameworkException frameworkException) {
        //never happens
    }

    @Override
    public abstract void onSuccess(); //todo test this in all concrete packetlisteners (abstract)
}
