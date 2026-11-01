package com.infrahub.watcher.monitoring.api;

import java.time.Instant;

import com.infrahub.watcher.monitoring.domain.MonitoredService;
import com.infrahub.watcher.monitoring.domain.ServiceStatus;

/** API representation; keeps the JPA entity out of the HTTP contract. */
public record MonitoredServiceResponse(
        Long id,
        String name,
        String url,
        ServiceStatus lastStatus,
        Instant lastCheckedAt) {

    static MonitoredServiceResponse from(MonitoredService service) {
        return new MonitoredServiceResponse(
                service.getId(),
                service.getName(),
                service.getUrl(),
                service.getLastStatus(),
                service.getLastCheckedAt());
    }
}
