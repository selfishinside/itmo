package ru.itmo.soa.workers;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.util.Arrays;

@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION - 100)
public class TransportFilter implements ContainerRequestFilter, ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext request) {
        if (!request.getSecurityContext().isSecure()) {
            request.abortWith(Json.response(403, Json.error(403, "HTTPS is required")));
            return;
        }
        String origin = request.getHeaderString("Origin");
        if (origin != null && !allowed(origin)) {
            request.abortWith(Json.response(403, Json.error(403, "Origin is not allowed")));
            return;
        }
        if ("OPTIONS".equals(request.getMethod()) && origin != null
                && request.getHeaderString("Access-Control-Request-Method") != null) {
            request.abortWith(Response.noContent().build());
        }
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        String origin = request.getHeaderString("Origin");
        if (origin != null && allowed(origin)) {
            response.getHeaders().putSingle("Access-Control-Allow-Origin", origin);
            response.getHeaders().add("Vary", "Origin");
            response.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.getHeaders().putSingle("Access-Control-Allow-Headers", "Content-Type");
            response.getHeaders().putSingle("Access-Control-Expose-Headers", "Location");
        }
    }

    private boolean allowed(String origin) {
        return Arrays.stream(System.getProperty("cors.allowed.origins", "https://localhost:8443").split(","))
                .map(String::trim).anyMatch(value -> !value.isEmpty() && (value.equals(origin) || value.equals("*")));
    }
}
