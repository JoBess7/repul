package ca.ulaval.glo4003.repul;

import java.net.URI;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.config.context.ApplicationContext;

public class RepULServer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepULServer.class);

    private final ApplicationContext applicationContext;

    public RepULServer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void run() {
        Server server = initializeServer();
        start(server);
    }

    private Server initializeServer() {
        LOGGER.info("Setup http server");

        int port = URI.create(applicationContext.getURI()).getPort();
        Server server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(server, "/api");
        ResourceConfig resourceConfig = applicationContext.initializeResourceConfig();
        ServletContainer container = new ServletContainer(resourceConfig);
        ServletHolder servletHolder = new ServletHolder(container);

        context.addServlet(servletHolder, "/*");

        return server;
    }

    private void start(Server server) {
        try {
            LOGGER.info("Starting the server...");
            server.start();
            server.join();
        } catch (Exception e) {
            LOGGER.error("Error occurred while starting the server", e);
        } finally {
            if (server.isRunning()) {
                LOGGER.info("Destroying the server...");
                server.destroy();
                LOGGER.info("Server destroyed.");
            }
        }
    }
}
