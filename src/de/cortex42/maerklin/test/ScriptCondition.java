package de.cortex42.maerklin.test;

import java.util.function.BooleanSupplier;

/**
 * Created by ivo on 18.11.15.
 */
public class ScriptCondition {
    private final BooleanSupplier booleanSupplier;

    public ScriptCondition(BooleanSupplier booleanSupplier) {
        this.booleanSupplier = booleanSupplier;
    }

    public boolean check() {
        return booleanSupplier.getAsBoolean();
    }
}
