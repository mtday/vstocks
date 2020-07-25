package vstocks.rest;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class Application extends ResourceConfig {
    public Application() {
        property("jersey.config.server.wadl.disableWadl", "true");

        packages(true, Application.class.getPackageName());

        register(new DependencyInjectionBinder());
    }
}
