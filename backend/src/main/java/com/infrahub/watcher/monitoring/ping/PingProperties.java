package com.infrahub.watcher.monitoring.ping;

import java.time.Duration;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 * Runtime settings for the HTTP probe. The schedule itself ({@code watcher.ping.enabled},
 * {@code interval}, {@code initial-delay}) is read directly by {@link PingConfiguration} and
 * {@link PingService}'s {@code @Scheduled} annotation.
 */
@Validated
@ConfigurationProperties(prefix = "watcher.ping")
public record PingProperties(
        @NotNull @DefaultValue("PT5S") Duration connectTimeout,
        @NotNull @DefaultValue("PT10S") Duration requestTimeout,
        @Positive @DefaultValue("50") int maxConcurrency) {
}
