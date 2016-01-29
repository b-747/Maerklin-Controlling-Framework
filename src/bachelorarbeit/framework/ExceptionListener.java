package bachelorarbeit.framework;

import java.util.EventListener;

/**
 * Created by ivo on 06.12.15.
 */
public interface ExceptionListener extends EventListener {
    void onException(FrameworkException frameworkException);
}
