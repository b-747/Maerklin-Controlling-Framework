package de.cortex42.maerklin.test;

/**
 * Created by ivo on 13.11.15.
 */
public class TestScripts {
    public static Script getFineScript() {
        ScriptElement last;
        Script s = new Script();
        last = s.first = new ScriptElementSwitch(3, 1, 200);
        last = last.next = new ScriptElementWait(15000);
        last.next = new ScriptElementSwitch(3, 0, 200);

        return s;
    }
}
