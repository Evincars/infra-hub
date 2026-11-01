package com.infrahub.watcher.monitoring.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

/** Request body for creating (POST) or fully replacing (PUT) a monitored service. */
public record MonitoredServiceRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 2048)
        @URL
        @Pattern(regexp = "(?i)^https?://.+", message = "must be an http or https URL")
        String url) {
}
