package bachelorarbeit.framework.scripting;

import bachelorarbeit.framework.CS2CANCommands;
import bachelorarbeit.framework.FrameworkException;

/**
 * Created by ivo on 13.11.15.
 */
public class ScriptElementSwitch extends ScriptElement {
    public enum SwitchPosition {
        ROUND,
        STRAIGHT
    }

    private final static long DELAY = 200L;
    private final int equipmentUid;
    private final SwitchPosition switchPosition;
    private final long switchDelay;

    public ScriptElementSwitch(final int equipmentUid, final SwitchPosition switchPosition) {
        this(equipmentUid, switchPosition, DELAY);
    }

    public ScriptElementSwitch(final int equipmentUid, final SwitchPosition switchPosition, final long switchDelay) {
        this.equipmentUid = equipmentUid;
        this.switchPosition = switchPosition;
        this.switchDelay = switchDelay;
    }

    @Override
    public void executeElement(final ScriptContext scriptContext) throws FrameworkException {
        scriptContext.sendCANPacket(
                CS2CANCommands.toggleEquipment(equipmentUid, switchPosition == SwitchPosition.ROUND ? CS2CANCommands.SWITCH_POSITION_ROUND : CS2CANCommands.SWITCH_POSITION_STRAIGHT, CS2CANCommands.EQUIPMENT_POWER_ON)
        );

        try {
            Thread.sleep(switchDelay);
        } catch (final InterruptedException e) {
            throw new FrameworkException(e);
        }

        scriptContext.sendCANPacket(
                CS2CANCommands.toggleEquipment(equipmentUid, switchPosition == SwitchPosition.ROUND ? CS2CANCommands.SWITCH_POSITION_ROUND : CS2CANCommands.SWITCH_POSITION_STRAIGHT, CS2CANCommands.EQUIPMENT_POWER_OFF)
        );
    }
}
