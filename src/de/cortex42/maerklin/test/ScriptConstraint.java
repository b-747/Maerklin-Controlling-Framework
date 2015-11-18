package de.cortex42.maerklin.test;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptConstraint extends ScriptElement {
    private final ScriptEvent scriptEvent;
    private ScriptConstraint otherScriptConstraint = null;
    private boolean isOr = false;
    private boolean isAnd = false;
    private boolean isXor = false;

    public ScriptConstraint(ScriptEvent scriptEvent) {
        this.scriptEvent = scriptEvent;
    }

    //todo prevent multiple calls
    public void or(ScriptConstraint scriptConstraint) {
        otherScriptConstraint = scriptConstraint;
        isOr = true;
    }

    public void and(ScriptConstraint scriptConstraint) {
        otherScriptConstraint = scriptConstraint;
        isAnd = true;
    }

    public void xor(ScriptConstraint scriptConstraint) {
        otherScriptConstraint = scriptConstraint;
        isXor = true;
    }

    @Override
    public void executeElement(ScriptContext scriptContext) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                if (scriptEvent.waitFor()) {

                } else if ( isXor && executeElement(scriptContext);)
                { //scriptEvent did not happen, now check if xor is set
                    //todo
                }else{
                    next = null; //end further execution of script
                }
            }
        })).start();
    }
}
