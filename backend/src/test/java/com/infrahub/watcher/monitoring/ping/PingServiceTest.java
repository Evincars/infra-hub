package com.infrahub.watcher.monitoring.ping;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import com.infrahub.watcher.monitoring.domain.MonitoredService;
import com.infrahub.watcher.monitoring.domain.ServiceStatus;
import com.infrahub.watcher.monitoring.persistence.MonitoredServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class PingServiceTest {

    private static final Instant NOW = Instant.parse("2026-01-01T12:00:00Z");

    private final MonitoredServiceRepository repository = mock(MonitoredServiceRepository.class);
    private final HttpProbe httpProbe = mock(HttpProbe.class);
    private PingService pingService;

    @BeforeEach
    void setUp() {
        PingProperties properties = new PingProperties(Duration.ofSeconds(1), Duration.ofSeconds(1), 4);
        pingService = new PingService(repository, httpProbe, properties, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void recordsResultForEveryService() throws Exception {
        MonitoredService up = service(1L, "https://up.example");
        MonitoredService down = service(2L, "https://down.example");
        when(repository.findAll()).thenReturn(List.of(up, down));
        when(httpProbe.probe("https://up.example")).thenReturn(ServiceStatus.UP);
        when(httpProbe.probe("https://down.example")).thenReturn(ServiceStatus.DOWN);

        pingService.pingAll();

        verify(repository).updateStatus(1L, ServiceStatus.UP, NOW);
        verify(repository).updateStatus(2L, ServiceStatus.DOWN, NOW);
    }

    @Test
    void oneFailingServiceDoesNotStopTheOthers() throws Exception {
        MonitoredService broken = service(1L, "https://broken.example");
        MonitoredService healthy = service(2L, "https://healthy.example");
        when(repository.findAll()).thenReturn(List.of(broken, healthy));
        when(httpProbe.probe("https://broken.example")).thenThrow(new IllegalStateException("boom"));
        when(httpProbe.probe("https://healthy.example")).thenReturn(ServiceStatus.UP);

        pingService.pingAll();

        verify(repository).updateStatus(2L, ServiceStatus.UP, NOW);
        verify(repository, never()).updateStatus(eq(1L), any(), any());
    }

    @Test
    void interruptedProbeLeavesStatusUntouched() throws Exception {
        MonitoredService service = service(1L, "https://slow.example");
        when(httpProbe.probe(any())).thenThrow(new InterruptedException());

        pingService.ping(service);

        verify(repository, never()).updateStatus(any(), any(), any());
        // clear the flag ping() restored so it doesn't leak into other tests
        Thread.interrupted();
    }

    private static MonitoredService service(Long id, String url) {
        MonitoredService service = new MonitoredService("svc-" + id, url);
        ReflectionTestUtils.setField(service, "id", id);
        return service;
    }
}
