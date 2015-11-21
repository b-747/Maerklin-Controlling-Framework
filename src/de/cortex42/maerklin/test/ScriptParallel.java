package de.cortex42.maerklin.test;

import java.util.ArrayList;

/**
 * Created by ivo on 20.11.15.
 */
public class ScriptParallel extends ScriptElement {
    private final ArrayList<ScriptConditionChecker> scriptConditionCheckers;

    public ScriptParallel(ArrayList<ScriptConditionChecker> scriptConditionCheckers) {
        this.scriptConditionCheckers = scriptConditionCheckers;
    }

    @Override
    public void executeElement(ScriptContext scriptContext) {
        final int scriptConditionCheckerCount = scriptConditionCheckers.size();
        boolean scriptConditionsResult = true;

        final Thread[] threads = new Thread[scriptConditionCheckerCount];

        for (int i = 0; i < threads.length; i++) {
            final int finalI = i;

            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    scriptConditionCheckers.get(finalI).execute(scriptContext);
                }
            });
            threads[i].start();

            try {
                threads[i].join(); //wait for all threads to finish
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scriptConditionsResult &= scriptConditionCheckers.get(finalI).isConditionMet(); //if one of the conditions was not met, then scriptConditionsResult is false
        }


        if (!scriptConditionsResult) {
            next = null; //stop further execution
        }
    }
}
