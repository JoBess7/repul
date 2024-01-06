package ca.ulaval.glo4003.repul.commons.api.exception.mapper;

import ca.ulaval.glo4003.repul.commons.api.exception.response.ErrorResponse;
import ca.ulaval.glo4003.repul.commons.domain.exception.RepULException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RepULExceptionMapper implements ExceptionMapper<RepULException> {
    @Override
    public Response toResponse(RepULException exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage());
        return Response.status(RepULExceptionStatusMapper.getResponseStatus(exception)).entity(response).build();
    }
}
