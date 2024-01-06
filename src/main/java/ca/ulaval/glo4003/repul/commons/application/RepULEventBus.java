package ca.ulaval.glo4003.repul.commons.application;

public interface RepULEventBus {
    void publish(RepULEvent event);

    void register(Object listener);

    void unregister(Object listener);
}
