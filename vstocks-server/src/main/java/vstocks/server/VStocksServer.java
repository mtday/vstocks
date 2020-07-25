package vstocks.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import vstocks.rest.Application;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;
import static vstocks.config.Config.*;

public class VStocksServer {
    public static void main(String... args) {
        String contextPath = SERVER_CONTEXT_PATH.getString();
        String apiPath = SERVER_API_PATH.getString();
        int port = SERVER_PORT.getInt();

        Server server = new Server(port);
        ServletContextHandler handler = new ServletContextHandler(NO_SESSIONS);
        handler.setContextPath(contextPath);
        server.setHandler(handler);

        ServletHolder servletHolder = handler.addServlet(ServletContainer.class, apiPath);
        servletHolder.setInitOrder(1);
        servletHolder.setInitParameter("javax.ws.rs.Application", Application.class.getName());

        try {
            server.start();
            server.join();
        } catch (Exception failed) {
            throw new RuntimeException("Failed to start server", failed);
        } finally {
            server.destroy();
        }
    }
}
