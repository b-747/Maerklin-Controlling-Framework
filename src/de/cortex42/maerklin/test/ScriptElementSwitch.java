package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.CS2CANCommands;
import de.cortex42.maerklin.framework.EthernetInterface;

import java.io.IOException;

/**
 * Created by ivo on 13.11.15.
 */
public class ScriptElementSwitch extends ScriptElement {
    private int equipmentId;
    private int position;
    private int switchDelay;

    public ScriptElementSwitch(int equipmentId, int position, int switchDelay) {
        this.equipmentId = equipmentId;
        this.position = position;
        this.switchDelay = switchDelay;
    }

    @Override
    public void execute() {
/*
        try {
            EthernetInterface ethernetInterface = EthernetInterface.getInstance(15730);
            ethernetInterface.writeCANPacket(CS2CANCommands.toggleEquipment(new byte[]{0x00, 0x00, 0x30, (byte) (equipmentId & 0xFF)}, (byte) (position & 0xFF), CS2CANCommands.EQUIPMENT_POWER_ON), "192.168.16.2", 15731);
            Thread.sleep((long) switchDelay);
            ethernetInterface.writeCANPacket(CS2CANCommands.toggleEquipment(new byte[]{0x00, 0x00, 0x30, (byte) (equipmentId & 0xFF)}, (byte) (position & 0xFF), CS2CANCommands.EQUIPMENT_POWER_OFF), "192.168.16.2", 15731);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

*/

    }
}
