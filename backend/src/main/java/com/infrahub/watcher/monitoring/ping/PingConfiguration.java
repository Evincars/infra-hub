package com.infrahub.watcher.monitoring.ping;

import java.net.http.HttpClient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration(proxyBeanMethods = false)
public class PingConfiguration {

    /** HttpClient is AutoCloseable (Java 21+), so Spring closes it on shutdown. */
    @Bean
    HttpClient pingHttpClient(PingProperties properties) {
        return HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /** Set {@code watcher.ping.enabled=false} to turn off the pinger (e.g. in tests or API-only instances). */
    @Configuration(proxyBeanMethods = false)
    @EnableScheduling
    @ConditionalOnProperty(prefix = "watcher.ping", name = "enabled", havingValue = "true", matchIfMissing = true)
    static class SchedulingConfiguration {
    }
}
