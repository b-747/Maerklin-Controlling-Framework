package bachelorarbeit.testscript;

import bachelorarbeit.framework.FrameworkException;
import bachelorarbeit.framework.scripting.*;

import java.util.ArrayList;

/**
 * Created by ivo on 13.11.15.
 */
public final class TestScript {
    private static final int STOP = 0;
    private static final int SLOW = 450;
    private static final int MEDIUM_FAST = 600;
    private static final int FAST = 800;

    private static final int LOCO_5 = 0x4005;
    private static final int LOCO_6 = 0x4006;
    private static final int LOCO_7 = 0x4007;

    private static final int RAILWAY_SWITCH_1 = 0x3000; //left
    private static final int RAILWAY_SWITCH_2 = 0x3001; //right
    private static final int RAILWAY_SWITCH_3 = 0x3002; //right
    private static final int RAILWAY_SWITCH_4 = 0x3003; //left
    private static final int RAILWAY_SWITCH_5 = 0x3004; //right
    private static final int RAILWAY_SWITCH_6 = 0x3005; //left

    private static final int CONTACT_1 = 0x110001;
    private static final int CONTACT_3 = 0x110003;
    private static final int CONTACT_4 = 0x110004;
    private static final int CONTACT_7 = 0x110007;
    private static final int CONTACT_8 = 0x110008;
    private static final int CONTACT_9 = 0x110009;
    private static final int CONTACT_10 = 0x11000A;
    private static final int CONTACT_12 = 0x11000C;
    private static final int CONTACT_1001 = 0x1103E9;
    private static final int CONTACT_1003 = 0x1103EB;
    private static final int CONTACT_1007 = 0x1103EF;
    private static final int CONTACT_1008 = 0x1103F0;

    private TestScript() {
    }

    public static Script getTestScript(final ScriptContext scriptContext) throws FrameworkException {
        ScriptElement last;
        final Script script = new Script(scriptContext);

        last = script.first = new ScriptElementSwitch(RAILWAY_SWITCH_1, ScriptElementSwitch.SwitchPosition.STRAIGHT); //railway switch 1 right
        last = last.next = new ScriptElementSwitch(RAILWAY_SWITCH_2, ScriptElementSwitch.SwitchPosition.ROUND); //railway switch 2 right
        last = last.next = new ScriptElementSwitch(RAILWAY_SWITCH_3, ScriptElementSwitch.SwitchPosition.ROUND); //railway switch 3 right
        last = last.next = new ScriptElementSwitch(RAILWAY_SWITCH_4, ScriptElementSwitch.SwitchPosition.STRAIGHT); //railway switch 4 right
        last = last.next = new ScriptElementSetVelocity(LOCO_5, SLOW); //loco 5 slow
        last = last.next = new ScriptElementWaitForContact(CONTACT_10); //reach contact 10
        last = last.next = new ScriptElementSetVelocity(LOCO_5, MEDIUM_FAST); //loco 5 medium fast
        last = last.next = new ScriptElementWaitForContact(CONTACT_12); //reach contact 12
        last = last.next = new ScriptElementSetVelocity(LOCO_5, SLOW); //loco 5 slow
        last = last.next = new ScriptElementWaitForContact(CONTACT_1008); //reach contact 1008
        last = last.next = new ScriptElementSetVelocity(LOCO_5, STOP); //loco 5 stop
        last = last.next = new ScriptElementSwitch(RAILWAY_SWITCH_2, ScriptElementSwitch.SwitchPosition.STRAIGHT); //railway switch 2 left
        last = last.next = new ScriptElementSwitch(RAILWAY_SWITCH_4, ScriptElementSwitch.SwitchPosition.ROUND); //railway switch 4 left
        last = last.next = new ScriptElementSetVelocity(LOCO_6, MEDIUM_FAST); //loco 6 medium fast
        last = last.next = new ScriptElementWaitForContact(CONTACT_9); //reach contact 9
        last = last.next = new ScriptElementSetVelocity(LOCO_6, FAST); //loco 6 fast
        last = last.next = new ScriptElementSwitch(RAILWAY_SWITCH_5, ScriptElementSwitch.SwitchPosition.ROUND); //railway switch 5 right
        last = last.next = new ScriptElementSwitch(RAILWAY_SWITCH_6, ScriptElementSwitch.SwitchPosition.ROUND); //railway switch 6 left
        last = last.next = new ScriptElementWaitForContact(CONTACT_1003); //reach contact 1003
        last = last.next = new ScriptElementSetVelocity(LOCO_6, MEDIUM_FAST); //loco 6 medium fast
        last = last.next = new ScriptElementWaitForContact(CONTACT_7); //reach contact 7
        last = last.next = new ScriptElementSetVelocity(LOCO_6, SLOW); //loco 6 slow
        last = last.next = new ScriptElementWaitForContact(CONTACT_3); //reach contact 3
        last = last.next = new ScriptElementSetVelocity(LOCO_6, STOP); //loco 6 stop
        last = last.next = new ScriptElementSwitch(RAILWAY_SWITCH_3, ScriptElementSwitch.SwitchPosition.STRAIGHT); //railway switch 3 left
        last = last.next = new ScriptElementSwitch(RAILWAY_SWITCH_6, ScriptElementSwitch.SwitchPosition.STRAIGHT); //railway switch 6 right
        last = last.next = new ScriptElementSetVelocity(LOCO_7, MEDIUM_FAST); //loco 7 medium fast
        last = last.next = new ScriptElementWaitForContact(CONTACT_1007); //reach contact 1007
        last = last.next = new ScriptElementSetFunction(LOCO_7, 3, ScriptElementSetFunction.ToggleState.ON); //loco 7 whistle
        last = last.next = new ScriptElementSetDirection(LOCO_5, ScriptElementSetDirection.Direction.BACKWARD); //loco 5 backward
        last = last.next = new ScriptElementSetVelocity(LOCO_5, SLOW); //loco 5 slow
        last = last.next = new ScriptElementWait(1400L); //wait 1.4 s
        last = last.next = new ScriptElementSetFunction(LOCO_7, 3, ScriptElementSetFunction.ToggleState.OFF); //loco 7 stop whistle
        last = last.next = new ScriptElementSwitch(RAILWAY_SWITCH_4, ScriptElementSwitch.SwitchPosition.STRAIGHT); //railway switch 4 right
        last = last.next = new ScriptElementSwitch(RAILWAY_SWITCH_3, ScriptElementSwitch.SwitchPosition.ROUND); //railway switch 3 right
        last = last.next = new ScriptElementSwitch(RAILWAY_SWITCH_2, ScriptElementSwitch.SwitchPosition.ROUND); //railway switch 2 right


        //-------------------------------parallel-------------------------------

        final ScriptElementCondition scriptElementConditionReach4 = new ScriptElementCondition(new ScriptBooleanEventContactReached(scriptContext, CONTACT_4)); //reach contact 4
        scriptElementConditionReach4.next = new ScriptElementSetVelocity(LOCO_7, STOP); //loco 7 stop

        final ScriptElementCondition scriptElementConditionReach8 = new ScriptElementCondition(new ScriptBooleanEventContactReached(scriptContext, CONTACT_8)); //reach contact 8
        scriptElementConditionReach8.next = new ScriptElementSetVelocity(LOCO_7, SLOW); //loco 7 slow
        scriptElementConditionReach8.next.next = scriptElementConditionReach4;

        final ScriptElementCondition scriptElementConditionReach1001Or1 =
                new ScriptElementCondition(new ScriptBooleanEventContactReached(scriptContext, CONTACT_1001)) //when reaching contact 1001
                        .or((new ScriptElementCondition(new ScriptBooleanEventContactReached(scriptContext, CONTACT_1))) //or ((reaching contact 1)
                                .and(new ScriptElementCondition(new ScriptBooleanEventContactFree(scriptContext, CONTACT_1, 200L)))); //and contact 1 remains free for 200ms)
        scriptElementConditionReach1001Or1.next = new ScriptElementSetVelocity(LOCO_5, STOP); //then stop loco 5

        final ArrayList<ScriptElement> scriptElements = new ArrayList<>(2);
        scriptElements.add(scriptElementConditionReach8);
        scriptElements.add(scriptElementConditionReach1001Or1);

        last = last.next = new ScriptElementParallel(scriptElements);

        //-------------------------------parallel-------------------------------


        last = last.next = new ScriptElementCondition(new ScriptBooleanEventTrainVelocity(scriptContext, LOCO_5, 0)) //loco 5 stopped
                .and(new ScriptElementCondition(new ScriptBooleanEventTrainVelocity(scriptContext, LOCO_7, 0))); //loco 7 stopped
        last = last.next = new ScriptElementSetDirection(LOCO_5, ScriptElementSetDirection.Direction.FORWARD); //loc 5 forward
        last = last.next = new ScriptElementSwitch(RAILWAY_SWITCH_2, ScriptElementSwitch.SwitchPosition.STRAIGHT); //railway switch 2 straight
        last = last.next = new ScriptElementSwitch(RAILWAY_SWITCH_5, ScriptElementSwitch.SwitchPosition.STRAIGHT); //railway switch 5 left
        last = last.next = new ScriptElementSwitch(RAILWAY_SWITCH_6, ScriptElementSwitch.SwitchPosition.ROUND); //railway switch 6 left
        last = last.next = new ScriptElementSetFunction(LOCO_6, 3, ScriptElementSetFunction.ToggleState.ON); //loco 6 whistle
        last = last.next = new ScriptElementWait(1400L); //wait 1.4s
        last = last.next = new ScriptElementSetFunction(LOCO_6, 3, ScriptElementSetFunction.ToggleState.OFF); //loco 6 stop whistle
        last = last.next = new ScriptElementSetVelocity(LOCO_6, MEDIUM_FAST); //loco 6 medium fast
        last = last.next = new ScriptElementWaitForContact(CONTACT_10); //reach contact 10
        last = last.next = new ScriptElementSetVelocity(LOCO_6, FAST); //loco 6 fast
        last = last.next = new ScriptElementWaitForContact(CONTACT_1003); //reach contact 1003
        last = last.next = new ScriptElementSetVelocity(LOCO_6, MEDIUM_FAST); //loco 6 medium fast
        last = last.next = new ScriptElementWaitForContact(CONTACT_7); //reach contact 7
        last = last.next = new ScriptElementSetVelocity(LOCO_6, SLOW); //loco 6 slow
        last = last.next = new ScriptElementWaitForContact(CONTACT_3); //reach contact 3
        last.next = new ScriptElementSetVelocity(LOCO_6, STOP); //loco 6 stop

        return script;
    }
}
