package bachelorarbeit.testscript;

import bachelorarbeit.framework.EthernetConnection;
import bachelorarbeit.framework.FrameworkException;
import bachelorarbeit.framework.scripting.Script;
import bachelorarbeit.framework.scripting.ScriptContext;

/**
 * Created by ivo on 13.11.15.
 */
public final class Main {
    public static void main(final String[] args) {
        final String CS2_IP_ADDRESS = "192.168.16.2";

        try (EthernetConnection ethernetConnection = new EthernetConnection(CS2_IP_ADDRESS)) {
            final Script script = TestScript.getTestScript(new ScriptContext(ethernetConnection));
            script.execute();
        } catch (final FrameworkException e) {
            e.printStackTrace();
        }
    }
}
