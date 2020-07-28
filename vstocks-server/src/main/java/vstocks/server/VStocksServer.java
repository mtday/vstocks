package vstocks.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.rest.Application;
import vstocks.tasks.MemoryUsageLoggingTask;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.*;
import static vstocks.config.Config.*;

public class VStocksServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(VStocksServer.class);

    public static void main(String... args) throws URISyntaxException, MalformedURLException {
        String contextPath = SERVER_CONTEXT_PATH.getString();
        String apiPath = SERVER_API_PATH.getString();
        int port = SERVER_PORT.getInt();

        URL staticResourceURL = ofNullable(VStocksServer.class.getResource("/META-INF/static/index.html"))
                .orElseThrow(() -> new RuntimeException("Failed to find META-INF static resources"));
        URI staticResourceURI = staticResourceURL.toURI().resolve("./");

        Server server = new Server(port);
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath(contextPath);
        servletContextHandler.setSessionHandler(new SessionHandler());
        servletContextHandler.setBaseResource(Resource.newResource(staticResourceURI));
        servletContextHandler.addServlet(new ServletHolder("default", DefaultServlet.class), contextPath);

        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, apiPath);
        servletHolder.setInitOrder(1);
        servletHolder.setInitParameter("javax.ws.rs.Application", Application.class.getName());

        HandlerList handlerList = new HandlerList();
        handlerList.addHandler(servletContextHandler);
        handlerList.addHandler(new DefaultHandler());

        server.setHandler(handlerList);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(8);

        // Determine how long to delay so that our scheduled tasks all run at approximately each 10 minute mark.
        LocalDateTime now = LocalDateTime.now();
        int minute = now.get(ChronoField.MINUTE_OF_HOUR) % 10;
        int second = now.get(ChronoField.SECOND_OF_MINUTE);
        long delaySeconds = ((9 - minute) * 60) + (second > 0 ? 60 - second : 60);

        executorService.scheduleAtFixedRate(new MemoryUsageLoggingTask(),
                SECONDS.toMillis(delaySeconds % MINUTES.toSeconds(2)), MINUTES.toMillis(2), MILLISECONDS);
        executorService.submit(() -> {
            try {
                server.start();
                LOGGER.info("Server started");
                server.join();
            } catch (Exception failed) {
                throw new RuntimeException("Failed to start server", failed);
            }
        });
    }
}
