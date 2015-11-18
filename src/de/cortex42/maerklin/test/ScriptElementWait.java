package de.cortex42.maerklin.test;

/**
 * Created by ivo on 13.11.15.
 */
public class ScriptElementWait extends ScriptElement {
    private long milliseconds;

    public ScriptElementWait(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    @Override
    public void executeElement(ScriptContext scriptContext) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
