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
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Optional.ofNullable;
import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;
import static vstocks.config.Config.*;

public class VStocksServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(VStocksServer.class);

    public static void main(String... args) {
        String contextPath = SERVER_CONTEXT_PATH.getString();
        String uiPath = SERVER_UI_PATH.getString();

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

        ServletContextHandler servletContextHandler = new ServletContextHandler(SESSIONS);
        servletContextHandler.setContextPath(contextPath);
        servletContextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());
        servletContextHandler.setSessionHandler(new SessionHandler());
        servletContextHandler.setBaseResource(Resource.newResource(Paths.get(uiPath)));
        servletContextHandler.addServlet(new ServletHolder("default", new DefaultServlet() {
            @Override
            public Resource getResource(String path) {
                // Any resource that does not have a file extension returns "/index.html". This allows refreshes
                // on any url within the Angular frontend.
                int dot = path.lastIndexOf('.');
                return dot < 0 ? super.getResource("/index.html") : super.getResource(path);
            }
        }), contextPath);

        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/api/*");
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
