package com.infrahub.watcher.monitoring.ping;

import java.time.Clock;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.infrahub.watcher.monitoring.domain.MonitoredService;
import com.infrahub.watcher.monitoring.domain.ServiceStatus;
import com.infrahub.watcher.monitoring.persistence.MonitoredServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Periodically checks every monitored URL and records the result.
 * <p>
 * Design notes:
 * <ul>
 *   <li>{@code fixedDelay} (not {@code fixedRate}) guarantees runs never overlap.</li>
 *   <li>URLs are probed in parallel on virtual threads, bounded by {@code watcher.ping.max-concurrency},
 *       so one slow endpoint can't delay the rest.</li>
 *   <li>No DB transaction is held during network I/O; each result is written with a small targeted update.</li>
 * </ul>
 * <p>
 * Pro juniora: {@code @Scheduled} nechá Spring pravidelně volat {@code pingAll}.
 * Fixed delay je pauza od konce jednoho běhu do začátku dalšího.
 * Virtuální vlákna jsou lehká vlákna vhodná pro čekání na síť; sama neomezují
 * počet souběžných HTTP požadavků, proto zde používáme {@code Semaphore}.
 * {@code @Slf4j} přes Lombok vytvoří logger {@code log}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PingService {

    private final MonitoredServiceRepository repository;
    private final HttpProbe httpProbe;
    private final PingProperties properties;
    private final Clock clock;

    @Scheduled(
            fixedDelayString = "${watcher.ping.interval:PT30S}",
            initialDelayString = "${watcher.ping.initial-delay:PT10S}")
    public void pingAll() {
        List<MonitoredService> services = repository.findAll();
        if (services.isEmpty()) {
            log.debug("No monitored services to ping");
            return;
        }

        long startedAt = System.nanoTime();
        Semaphore permits = new Semaphore(properties.maxConcurrency());

        // close() blocks until every submitted task has finished
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (MonitoredService service : services) {
                executor.submit(() -> pingWithPermit(service, permits));
            }
        }

        log.info("Pinged {} service(s) in {} ms",
                services.size(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt));
    }

    private void pingWithPermit(MonitoredService service, Semaphore permits) {
        try {
            permits.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        try {
            ping(service);
        } finally {
            permits.release();
        }
    }

    void ping(MonitoredService service) {
        try {
            ServiceStatus status = httpProbe.probe(service.getUrl());
            int updated = repository.updateStatus(service.getId(), status, clock.instant());

            if (updated > 0 && status != service.getLastStatus()) {
                log.info("Service '{}' ({}) changed {} -> {}",
                        service.getName(), service.getUrl(), service.getLastStatus(), status);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // shutting down: leave the last known status untouched
        } catch (RuntimeException e) {
            // Isolate failures so one bad row/DB hiccup doesn't abort the whole run.
            log.error("Failed to ping service id={} ({})", service.getId(), service.getUrl(), e);
        }
    }
}
