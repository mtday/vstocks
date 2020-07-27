package vstocks.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.pac4j.jax.rs.features.JaxRsConfigProvider;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider;
import org.pac4j.jax.rs.servlet.features.ServletJaxRsContextFactoryProvider;
import vstocks.rest.security.AccessLogFilter;
import vstocks.rest.security.SecurityConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class Application extends ResourceConfig {
    public Application() {
        property("jersey.config.server.wadl.disableWadl", "true");

        packages(true, Application.class.getPackageName());

        register(new AccessLogFilter());
        register(new DependencyInjectionBinder());

        register(new JaxRsConfigProvider(SecurityConfig.getConfig()));
        register(new Pac4JSecurityFeature());
        register(new Pac4JValueFactoryProvider.Binder());
        register(new ServletJaxRsContextFactoryProvider());
    }
}
