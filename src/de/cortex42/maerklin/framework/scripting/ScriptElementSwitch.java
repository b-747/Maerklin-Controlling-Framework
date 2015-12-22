package de.cortex42.maerklin.framework.scripting;

import de.cortex42.maerklin.framework.CS2CANCommands;
import de.cortex42.maerklin.framework.FrameworkException;

/**
 * Created by ivo on 13.11.15.
 */
public class ScriptElementSwitch extends ScriptElement {
    private final static long DELAY = 200L;
    private final int equipmentId;
    private final int position;
    private final long switchDelay;

    public ScriptElementSwitch(final int equipmentId, final int position) {
        this(equipmentId, position, DELAY);
    }

    public ScriptElementSwitch(final int equipmentId, final int position, final long switchDelay) {
        this.equipmentId = equipmentId;
        this.position = position;
        this.switchDelay = switchDelay;
    }

    @Override
    public void executeElement(final ScriptContext scriptContext) throws FrameworkException {
        scriptContext.writeCANPacket(
                CS2CANCommands.toggleEquipment(equipmentId, position, CS2CANCommands.EQUIPMENT_POWER_ON)
        );

        try {
            Thread.sleep(switchDelay);
        } catch (final InterruptedException e) {
            throw new FrameworkException(e);
        }

        scriptContext.writeCANPacket(
                CS2CANCommands.toggleEquipment(equipmentId, position, CS2CANCommands.EQUIPMENT_POWER_OFF)
        );
    }
}
