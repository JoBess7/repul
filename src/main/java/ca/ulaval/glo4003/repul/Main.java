package ca.ulaval.glo4003.repul;

import ca.ulaval.glo4003.repul.config.context.ApplicationContext;
import ca.ulaval.glo4003.repul.config.context.ApplicationContextFactory;

/**
 * RESTApi setup without using DI or spring.
 */
@SuppressWarnings("all")
public class Main {
    public static void main(String[] args) throws Exception {
        ApplicationContextFactory contextFactory = new ApplicationContextFactory();
        ApplicationContext applicationContext = contextFactory.createApplicationContext(args);
        Runnable server = new RepULServer(applicationContext);
        server.run();
    }
}
