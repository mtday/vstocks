package vstocks.rest;

import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;

import java.util.function.Function;

public class CommonProfileValueParamProvider implements ValueParamProvider {
    private final CommonProfile profile;

    public CommonProfileValueParamProvider(CommonProfile profile) {
        this.profile = profile;
    }

    @Override
    public Function<ContainerRequest, ?> getValueProvider(Parameter parameter) {
        if (parameter.isAnnotationPresent(Pac4JProfile.class)
                && CommonProfile.class.isAssignableFrom(parameter.getRawType())) {
            return request -> profile;
        }
        return null;
    }

    @Override
    public PriorityType getPriority() {
        return Priority.HIGH;
    }
}
