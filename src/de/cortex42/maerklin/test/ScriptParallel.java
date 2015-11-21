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
        int scriptConditionCount = scriptConditionCheckers.size();
        final WaitingThreadExchangeObject waitingThreadExchangeObject = new WaitingThreadExchangeObject();

        final Thread[] threads = new Thread[scriptConditionCount];

        for (int i = 0; i < threads.length; i++) {
            final int finalI = i;

            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean result = scriptConditionCheckers.get(finalI).check();

                    if (result) {
                        scriptConditionCheckers.get(finalI).execute(scriptContext);
                    }

                    synchronized (waitingThreadExchangeObject) {
                        waitingThreadExchangeObject.value &= result;
                    }
                }
            });
            threads[i].start();

            try {
                threads[i].join(); //wait for all threads to finish
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!waitingThreadExchangeObject.value) {
            next = null; //stop further execution
        }
    }
}
