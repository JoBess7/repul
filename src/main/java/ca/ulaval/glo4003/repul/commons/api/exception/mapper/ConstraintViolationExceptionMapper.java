package ca.ulaval.glo4003.repul.commons.api.exception.mapper;

import ca.ulaval.glo4003.repul.commons.api.exception.response.ErrorResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        ErrorResponse response = new ErrorResponse(getErrorMessage(exception));
        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }

    private String getErrorMessage(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream().map(ConstraintViolation::getMessage).findFirst().orElse("Invalid request");
    }
}
