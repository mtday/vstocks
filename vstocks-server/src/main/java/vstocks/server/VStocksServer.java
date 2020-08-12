package vstocks.server;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.rest.Application;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Optional.ofNullable;
import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;
import static vstocks.config.Config.*;

public class VStocksServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(VStocksServer.class);

    public static void main(String... args) throws URISyntaxException, MalformedURLException {
        String contextPath = SERVER_CONTEXT_PATH.getString();
        String apiPath = SERVER_API_PATH.getString();

        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSecureScheme("https");
        httpConfiguration.setSecurePort(SERVER_PORT.getInt());
        httpConfiguration.addCustomizer(new SecureRequestCustomizer());

        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(getFilePath(SERVER_KEYSTORE_FILE.getString()));
        sslContextFactory.setKeyStoreType(SERVER_KEYSTORE_TYPE.getString());
        sslContextFactory.setKeyStorePassword(SERVER_KEYSTORE_PASSWORD.getString());
        sslContextFactory.setKeyManagerPassword(SERVER_KEYSTORE_PASSWORD.getString());
        sslContextFactory.setTrustStorePath(getFilePath(SERVER_TRUSTSTORE_FILE.getString()));
        sslContextFactory.setTrustStoreType(SERVER_TRUSTSTORE_TYPE.getString());
        sslContextFactory.setTrustStorePassword(SERVER_TRUSTSTORE_PASSWORD.getString());

        Server server = new Server();

        ServerConnector sslConnector = new ServerConnector(
                server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(httpConfiguration)
        );
        sslConnector.setPort(SERVER_PORT.getInt());
        server.addConnector(sslConnector);

        URL staticResourceURL = ofNullable(VStocksServer.class.getResource("/META-INF/static/index.html"))
                .orElseThrow(() -> new RuntimeException("Failed to find META-INF static resources"));
        URI staticResourceURI = staticResourceURL.toURI().resolve("./");

        ServletContextHandler servletContextHandler = new ServletContextHandler(SESSIONS);
        servletContextHandler.setContextPath(contextPath);
        servletContextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());
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
    }

    private static String getFilePath(String file) {
        if (new File(file).exists()) {
            return file;
        }

        ClassLoader classLoader = VStocksServer.class.getClassLoader();
        return ofNullable(classLoader.getResource(file))
                .map(URL::toString)
                .orElseThrow(() -> new RuntimeException("Failed to find file: " + file));
    }
}
