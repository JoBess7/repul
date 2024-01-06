package ca.ulaval.glo4003.repul.commons.infrastructure;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;

import com.google.common.eventbus.GuavaExceptionPropagatingEventBus;

public class GuavaEventBus implements RepULEventBus {
    private final GuavaExceptionPropagatingEventBus eventBus;

    public GuavaEventBus() {
        eventBus = new GuavaExceptionPropagatingEventBus();
    }

    @Override
    public void publish(RepULEvent event) {
        eventBus.post(event);
    }

    @Override
    public void register(Object listener) {
        eventBus.register(listener);
    }

    @Override
    public void unregister(Object listener) {
        eventBus.unregister(listener);
    }
}
