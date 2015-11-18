package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.CANPacket;
import de.cortex42.maerklin.framework.CS2CANCommands;
import de.cortex42.maerklin.framework.PacketEvent;
import de.cortex42.maerklin.framework.PacketListener;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptElementWaitForContact extends ScriptElement {
    private final int contactId;
    private final int switchOverTo;

    public ScriptElementWaitForContact(int contactId, int switchOverTo) {
        this.contactId = contactId;
        this.switchOverTo = switchOverTo;
    }

    @Override
    public void executeElement(ScriptContext scriptContext) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean[] contactReached = {false};

                scriptContext.addPacketListener(
                        new PacketListener() {
                            @Override
                            public void packetEvent(PacketEvent packetEvent) {
                                CANPacket canPacket = packetEvent.getCANPacket();

                                if (canPacket.getCommand() == (CS2CANCommands.S88_EVENT & 1) //for response bit
                                        && canPacket.getDlc() == CS2CANCommands.S88_EVENT_RESPONSE_DLC
                                        && canPacket.getID() == contactId
                                        && ((canPacket.getData()[5] & 0xFF) == switchOverTo)) {

                                    contactReached[0] = true;
                                    scriptContext.removePacketListener(this);
                                }
                            }
                        });

                while (!contactReached[0]) ; //"sleep" until contact is reached
            }
        })).start();
    }
}
