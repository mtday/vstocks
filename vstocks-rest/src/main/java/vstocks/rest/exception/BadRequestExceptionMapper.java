package vstocks.rest.exception;

import vstocks.model.ErrorResponse;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
    @Override
    public Response toResponse(BadRequestException exception) {
        ErrorResponse errorResponse = new ErrorResponse()
                .setStatus(BAD_REQUEST.getStatusCode())
                .setMessage(exception.getMessage());
        return Response.status(BAD_REQUEST).entity(errorResponse).build();
    }
}
