package ca.ulaval.glo4003.repul.fixture.commons;

import java.net.URI;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import ca.ulaval.glo4003.repul.config.context.ApplicationContext;

public class ServerFixture {
    private final Server server;

    public ServerFixture(ApplicationContext applicationContext) {
        server = new Server(URI.create(applicationContext.getURI()).getPort());

        ServletContextHandler context = new ServletContextHandler(server, "/api");
        ResourceConfig resourceConfig = applicationContext.initializeResourceConfig();
        ServletContainer container = new ServletContainer(resourceConfig);
        ServletHolder servletHolder = new ServletHolder(container);

        context.addServlet(servletHolder, "/*");
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }
}
