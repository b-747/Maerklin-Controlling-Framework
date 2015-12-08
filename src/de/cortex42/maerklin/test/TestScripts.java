package de.cortex42.maerklin.test;

import de.cortex42.maerklin.framework.CS2CANCommands;
import de.cortex42.maerklin.framework.FrameworkException;
import de.cortex42.maerklin.framework.scripting.*;

import java.util.ArrayList;

/**
 * Created by ivo on 13.11.15.
 */
public class TestScripts {
    private static final int STOPP = 0;
    private static final int LANGSAM = 450;
    private static final int MITTELSCHNELL = 600;
    private static final int SCHNELL = 800;

    public static Script getTestScript(ScriptContext scriptContext) throws FrameworkException {
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
        last = last.next = new ScriptElementSetFunction(0x4007, 3, 1); //Lok 7 Pfeifen
        last = last.next = new ScriptElementSetDirection(0x4005, CS2CANCommands.DIRECTION_BACKWARD); //Lok 5 rückwärts
        last = last.next = new ScriptElementSetVelocity(0x4005, LANGSAM); //Lok 5 fährt langsam los
        last = last.next = new ScriptElementWait(1400L); //1,4 s warten
        last = last.next = new ScriptElementSetFunction(0x4007, 3, 0); //Lok 7 Pfeifen aus
        last = last.next = new ScriptElementSwitch(0x3003, 1); //Weiche 4 rechts
        last = last.next = new ScriptElementSwitch(0x3002, 0); //Weiche 3 rechts
        last = last.next = new ScriptElementSwitch(0x3001, 0); //Weiche 2 rechts

        //----gleichzeitige Beobachtung
        ArrayList<ScriptElement> scriptElementConditionCheckers = new ArrayList<>();

        ScriptElementConditionChecker scriptElementConditionChecker1 = new ScriptElementConditionChecker(new ScriptCondition(new ScriptBooleanEventContactReached(scriptContext, 0x110008))); //Erreichen von Kontakt 8
        scriptElementConditionChecker1.next = new ScriptElementSetVelocity(0x4007, LANGSAM); //Lok 7 wird langsam

        ScriptElementConditionChecker temp = new ScriptElementConditionChecker(new ScriptCondition(new ScriptBooleanEventContactReached(scriptContext, 0x110004))); //Erreichen von Kontakt 4
        temp.next = new ScriptElementSetVelocity(0x4007, STOPP); //Lok 7 hält an
        scriptElementConditionChecker1.next.next = temp;


        /* Bei ((Erreichen von Kontakt 1001) oder (Erreichen von Kontakt 1 und
            danach mindestens 200ms Freigabe von Kontakt 1)) bleibt Lok 5 stehen.*/

        ScriptElementConditionChecker scriptElementConditionChecker3 = new ScriptElementConditionChecker(
                new ScriptCondition(new ScriptBooleanEventContactReached(scriptContext, 0x1103E9)) //Erreichen von Kontakt 1001
                        .or((new ScriptCondition(new ScriptBooleanEventContactReached(scriptContext, 0x110001))) //oder ((Erreichen von Kontakt 1)
                                .and(new ScriptCondition(new ScriptBooleanEventContactFree(scriptContext, 0x110001, 200L)))) //und 200ms Freigabe von Kontakt 1)
        );

        scriptElementConditionChecker3.next = new ScriptElementSetVelocity(0x4005, STOPP); //Lok 5 hält an

        scriptElementConditionCheckers.add(scriptElementConditionChecker1);
        scriptElementConditionCheckers.add(scriptElementConditionChecker3);

        last = last.next = new ScriptElementParallel(scriptElementConditionCheckers);
        //----gleichzeitige Beobachtung

        ScriptElementConditionChecker scriptElementConditionCheckerStop5And7 = new ScriptElementConditionChecker(
                new ScriptCondition(new ScriptBooleanEventTrainVelocity(scriptContext, 0x4005, 0))
                        .and(new ScriptCondition(new ScriptBooleanEventTrainVelocity(scriptContext, 0x4007, 0)))
        );

        last = last.next = scriptElementConditionCheckerStop5And7;
        //if scriptElementConditionCheckerStop5And7 then
        last = last.next = new ScriptElementSetDirection(0x4005, CS2CANCommands.DIRECTION_FORWARD); //Lok 5 vorwärts (und bleibt stehen)
        last = last.next = new ScriptElementSwitch(0x3001, 1); //Weiche 2 gerade
        last = last.next = new ScriptElementSwitch(0x3004, 1); //Weiche 5 links
        last = last.next = new ScriptElementSwitch(0x3005, 0); //Weiche 6 links
        last = last.next = new ScriptElementSetFunction(0x4006, 3, 1); //Lok 6 Pfeifen
        last = last.next = new ScriptElementWait(1400L); //1,4 s warten
        last = last.next = new ScriptElementSetFunction(0x4006, 3, 0); //Lok 6 Pfeifen
        last = last.next = new ScriptElementSetVelocity(0x4006, MITTELSCHNELL); //Lok 6 wird mittelschnell
        last = last.next = new ScriptElementWaitForContact(0x11000A, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 10
        last = last.next = new ScriptElementSetVelocity(0x4006, SCHNELL); //Lok 6 wird schnell
        last = last.next = new ScriptElementWaitForContact(0x1103EB, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 1003
        last = last.next = new ScriptElementSetVelocity(0x4006, MITTELSCHNELL); //Lok 6 wird mittelschnell
        last = last.next = new ScriptElementWaitForContact(0x110007, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 7
        last = last.next = new ScriptElementSetVelocity(0x4006, LANGSAM); //Lok 6 wird langsam
        last = last.next = new ScriptElementWaitForContact(0x110003, CS2CANCommands.EQUIPMENT_POSITION_ON); //Erreichen von Kontakt 3
        last.next = new ScriptElementSetVelocity(0x4006, STOPP); //Lok 6 hält an

        return s;
    }
}
