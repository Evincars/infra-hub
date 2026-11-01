package com.infrahub.watcher.monitoring.ping;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.infrahub.watcher.monitoring.domain.ServiceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Performs a single HTTP check against a URL. Has no knowledge of persistence. */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpProbe {

    private static final String USER_AGENT = "ServiceWatcher/1.0";

    private final HttpClient httpClient;
    private final PingProperties properties;

    /**
     * @return {@link ServiceStatus#UP} for a 2xx/3xx response (after following redirects),
     *         otherwise {@link ServiceStatus#DOWN}
     * @throws InterruptedException if the calling thread is interrupted (e.g. on shutdown);
     *         the caller should not record a result in that case
     */
    public ServiceStatus probe(String url) throws InterruptedException {
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(properties.requestTimeout())
                    .header("User-Agent", USER_AGENT)
                    .GET() // HEAD is cheaper but too many servers reject or mishandle it
                    .build();
        } catch (IllegalArgumentException e) {
            log.warn("Invalid URL '{}': {}", url, e.getMessage());
            return ServiceStatus.DOWN;
        }

        try {
            int statusCode = httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
            log.debug("{} -> HTTP {}", url, statusCode);
            return statusCode < 400 ? ServiceStatus.UP : ServiceStatus.DOWN;
        } catch (IOException e) {
            // Covers connection refused, DNS failure, TLS errors and HttpTimeoutException.
            log.debug("{} -> unreachable: {}", url, e.toString());
            return ServiceStatus.DOWN;
        }
    }
}
