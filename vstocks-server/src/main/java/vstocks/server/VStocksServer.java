package vstocks.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.rest.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;
import static vstocks.config.Config.*;

public class VStocksServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(VStocksServer.class);

    public static void main(String... args) throws InterruptedException {
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

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> {
            try {
                server.start();
                LOGGER.info("Server started");
                server.join();
            } catch (Exception failed) {
                throw new RuntimeException("Failed to start server", failed);
            }
        });
        executorService.shutdown();

        while (!executorService.isTerminated()) {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
