package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.CS2CANCommands;

/**
 * Created by ivo on 13.11.15.
 */
public class ScriptElementSwitch extends ScriptElement {
    private final static long DELAY = 200L;
    private int equipmentId;
    private int position;
    private long switchDelay;

    public ScriptElementSwitch(int equipmentId, int position) {
        this(equipmentId, position, DELAY);
    }

    public ScriptElementSwitch(int equipmentId, int position, long switchDelay) {
        this.equipmentId = equipmentId;
        this.position = position;
        this.switchDelay = switchDelay;
    }

    @Override
    public void executeElement(ScriptContext scriptContext) {
        scriptContext.writeCANPacket(
                CS2CANCommands.toggleEquipment(equipmentId, position, CS2CANCommands.EQUIPMENT_POWER_ON)
        );

        try {
            Thread.sleep(switchDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scriptContext.writeCANPacket(
                CS2CANCommands.toggleEquipment(equipmentId, position, CS2CANCommands.EQUIPMENT_POWER_OFF)
        );
    }
}
