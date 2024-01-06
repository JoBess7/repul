package ca.ulaval.glo4003.repul.config.context;

import java.util.Arrays;

public class ApplicationContextFactory {
    private static final String TEST_COMMAND = "--test";
    private static final String DEMO_COMMAND = "--demo";

    public static ApplicationContext createApplicationContext(String[] commandLineArguments) {
        if (Arrays.stream(commandLineArguments).toList().contains(TEST_COMMAND)) {
            return new TestApplicationContext();
        } else if (Arrays.stream(commandLineArguments).toList().contains(DEMO_COMMAND)) {
            return new DemoApplicationContext();
        }
        return new DevApplicationContext();
    }
}
