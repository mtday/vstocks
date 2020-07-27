package vstocks.rest.resource.v1.security;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.rest.resource.BaseResource;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/security/login")
@Singleton
public class Login extends BaseResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);

    @GET
    @Path("/twitter")
    @Produces(APPLICATION_JSON)
    @Pac4JSecurity(clients = "TwitterClient", authorizers = "isAuthenticated")
    public Response twitterLogin(@Context UriInfo uriInfo, @Pac4JProfile CommonProfile profile) {
        LOGGER.info("Request URI: {}", uriInfo.getRequestUri());
        LOGGER.info("Profile Username:     {}", profile.getUsername());
        LOGGER.info("Profile Display Name: {}", profile.getDisplayName());
        LOGGER.info("Profile Email:        {}", profile.getEmail());
        LOGGER.info("Profile First Name:   {}", profile.getFirstName());
        LOGGER.info("Profile Family Name:  {}", profile.getFamilyName());
        LOGGER.info("Profile Gender:       {}", profile.getGender());
        LOGGER.info("Profile Profile URL:  {}", profile.getProfileUrl());
        LOGGER.info("Profile Picture URL:  {}", profile.getPictureUrl());
        LOGGER.info("Profile Location:     {}", profile.getLocation());
        LOGGER.info("Profile Locale:       {}", profile.getLocale());
        LOGGER.info("Profile Attributes:   {}", profile.getAttributes());
        LOGGER.info("Profile Client Name:  {}", profile.getClientName());
        LOGGER.info("Profile ID:           {}", profile.getId());
        LOGGER.info("Profile Linked ID:    {}", profile.getLinkedId());
        LOGGER.info("Profile Roles:        {}", profile.getRoles());
        LOGGER.info("Profile Permissions:  {}", profile.getPermissions());

        URI redirectUri = UriBuilder.fromUri(uriInfo.getRequestUri()).replacePath("/app/index.html").build();
        return Response.temporaryRedirect(redirectUri).build();
    }
}
