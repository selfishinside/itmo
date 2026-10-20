package ru.itmo.soa.workers;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger LOG = Logger.getLogger(ApiExceptionMapper.class.getName());

    @Override
    public Response toResponse(Exception error) {
        int status;
        String message;
        if (error instanceof ApiException api) {
            status = api.status();
            message = api.getMessage();
        } else if (error instanceof WebApplicationException web) {
            status = web.getResponse().getStatus();
            message = web.getResponse().getStatusInfo().getReasonPhrase();
        } else {
            LOG.log(Level.SEVERE, "Unhandled workers request error", error);
            status = 500;
            message = "Internal server error";
        }
        Response.ResponseBuilder response = Response.fromResponse(Json.response(status, Json.error(status, message)));
        if (error instanceof WebApplicationException web) {
            for (String header : new String[]{"Allow", "WWW-Authenticate", "Retry-After"}) {
                Object value = web.getResponse().getHeaders().getFirst(header);
                if (value != null) response.header(header, value);
            }
        }
        return response.build();
    }
}
