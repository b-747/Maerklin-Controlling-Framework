package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.CS2CANCommands;
import de.cortex42.maerklin.testgui.DebugOutput;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

/**
 * Created by ivo on 13.11.15.
 */
public class TestScripts {
    private static final int STOPP = 0;
    private static final int LANGSAM = 400;
    private static final int MITTELSCHNELL = 600;
    private static final int SCHNELL = 800;

    public static Script getTestScript(ScriptContext scriptContext) {
        ScriptElement last;
        Script s = new Script(scriptContext);

        last = s.first = new ScriptElementSwitch(0x3000, 1); //Weiche 1 rechts
        last = last.next = new ScriptElementSwitch(0x3001, 0); //Weiche 2 rechts
        last = last.next = new ScriptElementSwitch(0x3002, 0); //Weiche 3 rechts
        last = last.next = new ScriptElementSwitch(0x3003, 1); //Weiche 4 rechts
        last = last.next = new ScriptElementSetVelocity(0x4005, LANGSAM); //Lok 5 fährt langsam los
        last = last.next = new ScriptElementWaitForContact(0x11000A, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 10
        last = last.next = new ScriptElementSetVelocity(0x4005, MITTELSCHNELL); //Lok 5 wird mittelschnell
        last = last.next = new ScriptElementWaitForContact(0x11000C, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 12
        last = last.next = new ScriptElementSetVelocity(0x4005, LANGSAM); //Lok 5 wird langsam
        last = last.next = new ScriptElementWaitForContact(0x1103F0, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 1008
        last = last.next = new ScriptElementSetVelocity(0x4005, STOPP); //Lok 5 bleibt stehen
        last = last.next = new ScriptElementSwitch(0x3001, 1); //Weiche 2 links
        last = last.next = new ScriptElementSwitch(0x3003, 0); //Weiche 4 links
        last = last.next = new ScriptElementSetVelocity(0x4006, MITTELSCHNELL); //Lok 6 fährt mittelschnell los
        last = last.next = new ScriptElementWaitForContact(0x110009, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 9
        last = last.next = new ScriptElementSetVelocity(0x4006, SCHNELL); //Lok 6 wird schnell
        last = last.next = new ScriptElementSwitch(0x3004, 0); //Weiche 5 rechts
        last = last.next = new ScriptElementSwitch(0x3005, 0); //Weiche 6 links
        last = last.next = new ScriptElementWaitForContact(0x1103EB, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 1003
        last = last.next = new ScriptElementSetVelocity(0x4006, MITTELSCHNELL); //Lok 6 wird mittelschnell
        last = last.next = new ScriptElementWaitForContact(0x110007, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 7
        last = last.next = new ScriptElementSetVelocity(0x4006, LANGSAM); //Lok 6 wird langsam
        last = last.next = new ScriptElementWaitForContact(0x110003, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 3
        last = last.next = new ScriptElementSetVelocity(0x4006, STOPP); //Lok 6 bleibt stehen
        last = last.next = new ScriptElementSwitch(0x3002, 1); //Weiche 3 links
        last = last.next = new ScriptElementSwitch(0x3005, 1); //Weiche 6 rechts
        last = last.next = new ScriptElementSetVelocity(0x4007, MITTELSCHNELL); //Lok 7 fährt mittelschnell los
        last = last.next = new ScriptElementWaitForContact(0x1103EF, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 1007
        last = last.next = new ScriptElementSwitch(0x3003, 1); //Weiche 4 rechts
        last = last.next = new ScriptElementSwitch(0x3002, 0); //Weiche 3 rechts
        last = last.next = new ScriptElementSwitch(0x3001, 0); //Weiche 2 rechts
        last = last.next = new ScriptElementSetDirection(0x4005, CS2CANCommands.DIRECTION_BACKWARD); //Lok 5 rückwärts
        last = last.next = new ScriptElementSetVelocity(0x4005, LANGSAM); //Lok 5 fährt langsam los
        //----gleichzeitige Beobachtung
        ArrayList<ScriptConditionChecker> scriptConditionCheckers = new ArrayList<>();

        ScriptConditionChecker scriptConditionChecker1 = new ScriptConditionChecker(new ScriptCondition(new ScriptBooleanSupplierContactReached(scriptContext, 0x110008))); //Erreichen von Kontakt 8
        scriptConditionChecker1.next = new ScriptElementSetVelocity(0x4007, LANGSAM); //Lok 7 wird langsam

        ScriptConditionChecker scriptConditionChecker2 = new ScriptConditionChecker(new ScriptCondition(new ScriptBooleanSupplierContactReached(scriptContext, 0x110004))); //Erreichen von Kontakt 4
        scriptConditionChecker2.next = new ScriptElementSetVelocity(0x4007, STOPP); //Lok 7 hält an

        ScriptConditionChecker scriptConditionChecker3 = new ScriptConditionChecker(new ScriptCondition(new ScriptBooleanSupplierContactReached(scriptContext, 0x1103E9))); //Erreichen von Kontakt 1001
        scriptConditionChecker3.next = new ScriptElementSetVelocity(0x4005, STOPP); //Lok 5 hält an

        scriptConditionCheckers.add(scriptConditionChecker1);
        scriptConditionCheckers.add(scriptConditionChecker2);
        scriptConditionCheckers.add(scriptConditionChecker3);

        last = last.next = new ScriptParallel(scriptConditionCheckers);
        //----gleichzeitige Beobachtung


       /* last = last.next = new ScriptElementWaitForContact(0x110008, CS2CANCommands.EQUIPMENT_POSITION_ON);
        last = last.next = new ScriptElementSetVelocity(0x4007, LANGSAM);
        last = last.next = new ScriptElementWaitForContact(0x110004, CS2CANCommands.EQUIPMENT_POSITION_ON);
        last = last.next = new ScriptElementSetVelocity(0x4007, STOPP);
        last = last.next = new ScriptElementWaitForContact(0x110005, CS2CANCommands.EQUIPMENT_POSITION_ON);
        last = last.next = new ScriptElementWait(2000L);
        last = last.next = new ScriptElementSetVelocity(0x4005, STOPP);
        last = last.next = new ScriptElementSetDirection(0x4005, CS2CANCommands.DIRECTION_FORWARD);*/

        last = last.next = new ScriptElementSetDirection(0x4005, CS2CANCommands.DIRECTION_FORWARD); //Lok 5 vorwärts (und bleibt stehen)
        last = last.next = new ScriptElementSwitch(0x3001, 0); //Weiche 2 rechts
        last = last.next = new ScriptElementSwitch(0x3004, 1); //Weiche 5 links
        last = last.next = new ScriptElementSwitch(0x3005, 0); //Weiche 6 links
        last = last.next = new ScriptElementSetVelocity(0x4006, MITTELSCHNELL); //Lok 6 wird mittelschnell
        last = last.next = new ScriptElementWaitForContact(0x1100A0, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 10
        last = last.next = new ScriptElementSetVelocity(0x4006, SCHNELL); //Lok 6 wird schnell
        last = last.next = new ScriptElementWaitForContact(0x1103EB, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 1003
        last = last.next = new ScriptElementSetVelocity(0x4006, MITTELSCHNELL); //Lok 6 wird mittelschnell
        last = last.next = new ScriptElementWaitForContact(0x110007, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 7
        last = last.next = new ScriptElementSetVelocity(0x4006, LANGSAM); //Lok 6 wird langsam
        last = last.next = new ScriptElementWaitForContact(0x110003, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 3
        last.next = new ScriptElementSetVelocity(0x4006, STOPP); //Lok 6 hält an

        return s;
    }

    public static volatile long waitingTime1 = 0L;

    public static Script getLittleTestScript(ScriptContext scriptContext) {
        ScriptElement last;
        Script script = new Script(scriptContext);

        last = script.first = new ScriptElementGo();
        last = last.next = new ScriptElementSetVelocity(78, LANGSAM);

        ScriptConditionChecker scriptConditionChecker1 = new ScriptConditionChecker(new ScriptCondition(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {

                while (waitingTime1 < 5000L) ;
                DebugOutput.write("ScriptConditionChecker1, waitingtime: " + waitingTime1);
                return waitingTime1 >= 5000L;
            }
        }));
        scriptConditionChecker1.next = new ScriptElementSetVelocity(78, MITTELSCHNELL);
        scriptConditionChecker1.and(new ScriptConditionChecker(new ScriptCondition(new BooleanSupplier() { //scriptConditionChecker1 blocks until both area true
            @Override
            public boolean getAsBoolean() {

                while (waitingTime1 < 10000L) ;
                DebugOutput.write("ScriptConditionChecker2, waitingtime: " + waitingTime1);

                return waitingTime1 >= 10000L;
            }
        })));

       /* ScriptConditionChecker scriptConditionChecker2 = new ScriptConditionChecker(new ScriptCondition(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {

                while(waitingTime1< 10000L);
                DebugOutput.write("ScriptConditionChecker2, waitingtime: "+waitingTime1);

                return waitingTime1 >= 10000L;
            }
        }));
        scriptConditionChecker2.next = new ScriptElementSetVelocity(78, SCHNELL);*/

        ScriptConditionChecker scriptConditionChecker3 = new ScriptConditionChecker(new ScriptCondition(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {

                while (waitingTime1 < 15000L) ;
                DebugOutput.write("ScriptConditionChecker3, waitingtime: " + waitingTime1);

                return waitingTime1 >= 15000;
            }
        }));
        scriptConditionChecker3.next = new ScriptElementSetVelocity(78, LANGSAM);

        ArrayList<ScriptConditionChecker> scriptConditionCheckers = new ArrayList<>(3);
        scriptConditionCheckers.add(scriptConditionChecker1);
        //scriptConditionCheckers.add(scriptConditionChecker2);
        scriptConditionCheckers.add(scriptConditionChecker3);

        last = last.next = new ScriptParallel(scriptConditionCheckers);
        last = last.next = new ScriptElementSetVelocity(78, STOPP);
        last = last.next = new ScriptElementWait(250L);
        last = last.next = new ScriptElementSetDirection(78, CS2CANCommands.DIRECTION_FORWARD);
        last = last.next = new ScriptElementWait(250L);
        last = last.next = new ScriptElementSetVelocity(78, LANGSAM);
        last = last.next = new ScriptElementWait(5000L);
        last.next = new ScriptElementStop();

        return script;
    }
}
