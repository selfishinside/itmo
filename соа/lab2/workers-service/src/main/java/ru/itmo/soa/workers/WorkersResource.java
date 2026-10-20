package ru.itmo.soa.workers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class WorkersResource {
    private final WorkerRepository repository;

    public WorkersResource(WorkerRepository repository) { this.repository = repository; }

    @GET @Path("workers")
    public Response list(@Context UriInfo uri) {
        return Json.response(200, repository.search(WorkerSearch.query(uri.getQueryParameters())));
    }

    @POST @Path("workers/search") @Consumes(MediaType.APPLICATION_JSON)
    public Response search(String body) { return Json.response(200, repository.search(Json.parse(body, 400))); }

    @GET @Path("organizations")
    public Response organizations() { return Json.response(200, repository.organizations()); }

    @GET @Path("organizations/{orgId}/workers")
    public Response organizationWorkers(@PathParam("orgId") String orgId, @Context UriInfo uri) {
        return Json.response(200, repository.organizationWorkers(id(orgId), WorkerSearch.query(uri.getQueryParameters())));
    }

    @PUT @Path("organizations/{orgId}/workers/{workerId}")
    public Response assign(@PathParam("orgId") String orgId, @PathParam("workerId") String workerId) {
        repository.assign(id(orgId), id(workerId));
        return Response.noContent().build();
    }

    @POST @Path("workers") @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String body, @Context UriInfo uri) {
        ObjectNode worker = repository.create(Json.parse(body, 422));
        return Response.fromResponse(Json.response(201, worker))
                .location(uri.getAbsolutePathBuilder().path(worker.get("id").asText()).build()).build();
    }

    @GET @Path("workers/{id}")
    public Response get(@PathParam("id") String id) { return Json.response(200, repository.get(id(id))); }

    @PUT @Path("workers/{id}") @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") String id, String body) {
        return Json.response(200, repository.update(id(id), Json.parse(body, 422)));
    }

    @DELETE @Path("workers/{id}")
    public Response delete(@PathParam("id") String id) {
        repository.delete(id(id));
        return Response.noContent().build();
    }

    @GET @Path("workers/salary/average")
    public Response average() {
        BigDecimal value = repository.average();
        return value == null ? Response.noContent().build() : Json.response(200, value);
    }

    @GET @Path("workers/position/max")
    public Response maxPosition() { return Json.response(200, repository.maxPosition()); }

    @GET @Path("workers/salary/below/{salary}/number")
    public Response countBelow(@PathParam("salary") String salary) {
        int value = (int) Validation.pathInteger(salary, Integer.MIN_VALUE, Integer.MAX_VALUE, 400, "salary");
        return Json.response(200, repository.countBelow(value));
    }

    @POST @Path("internal/organizations/{orgId}/index/{coeff}")
    public Response index(@PathParam("orgId") String orgId, @PathParam("coeff") String coeff,
                          @HeaderParam("X-Internal-Token") String token) {
        String expected = System.getProperty("workers.internal.token", "local-development-token");
        if (token == null || expected.isBlank() || !MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), token.getBytes(StandardCharsets.UTF_8))) {
            throw new ApiException(401, "Invalid internal token");
        }
        long organization = Validation.pathInteger(orgId, 1, Long.MAX_VALUE, 400, "orgId");
        ObjectNode result = repository.index(organization, coeff);
        return result.get("updatedCount").longValue() == 0 ? Response.noContent().build() : Json.response(200, result);
    }

    private static long id(String value) { return Validation.pathInteger(value, 1, Long.MAX_VALUE, 400, "id"); }
}
