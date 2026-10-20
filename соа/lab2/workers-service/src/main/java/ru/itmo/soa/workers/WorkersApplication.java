package ru.itmo.soa.workers;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("/api/v1")
public class WorkersApplication extends Application {
    private final Set<Object> singletons;

    public WorkersApplication() {
        singletons = Set.of(new WorkersResource(WorkerRepository.configured()), new ApiExceptionMapper(), new TransportFilter());
    }

    @Override
    public Set<Object> getSingletons() { return singletons; }
}
