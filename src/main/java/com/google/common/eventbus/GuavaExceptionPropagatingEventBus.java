package com.google.common.eventbus;

public class GuavaExceptionPropagatingEventBus extends EventBus {
    public GuavaExceptionPropagatingEventBus() {
        super("exceptionPropagatingEventBus");
    }

    @Override
    public void handleSubscriberException(Throwable exception, SubscriberExceptionContext context) {
        try {
            String className = exception.getClass().getName();
            Class<? extends RuntimeException> theExceptionClass = Class.forName(className).asSubclass(RuntimeException.class);
            throw theExceptionClass.cast(exception);
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new RuntimeException(exception);
        }
    }
}
