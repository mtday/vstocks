package vstocks.rest.resource.v1.user;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import vstocks.model.User;
import vstocks.rest.Application;
import vstocks.rest.CommonProfileValueParamProvider;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static vstocks.model.UserSource.TWITTER;

public class GetUserIT extends JerseyTest {
    @Override
    protected Application configure() {
        CommonProfile commonProfile = new CommonProfile();
        commonProfile.setClientName("TwitterClient");
        commonProfile.setId("12345");
        commonProfile.addAttribute("username", "username");
        commonProfile.addAttribute("display_name", "Display Name");

        Application application = new Application(null, false);
        application.register(new CommonProfileValueParamProvider(commonProfile));
        return application;
    }

    @Test
    public void testGetUser() {
        Response response = target("/v1/user").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        User fetched = response.readEntity(User.class);
        assertEquals("TWITTER:12345", fetched.getId());
        assertEquals("username", fetched.getUsername());
        assertEquals(TWITTER, fetched.getSource());
        assertEquals("Display Name", fetched.getDisplayName());
    }
}
