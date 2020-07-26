package vstocks.rest.exception;

import vstocks.model.ErrorResponse;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse()
                .setStatus(NOT_FOUND.getStatusCode())
                .setMessage(exception.getMessage());
        return Response.status(NOT_FOUND).entity(errorResponse).build();
    }
}
