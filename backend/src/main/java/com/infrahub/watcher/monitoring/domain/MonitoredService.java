package com.infrahub.watcher.monitoring.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A URL whose availability is periodically checked.
 * <p>
 * Deliberately not using {@code @Data}: generated equals/hashCode/setters on JPA entities
 * break identity semantics and let callers overwrite fields such as the id.
 */
@Entity
@Table(name = "monitored_service")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // required by JPA
public class MonitoredService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 2048)
    private String url;

    // Hibernate 6 maps STRING enums to a native MySQL ENUM by default; force VARCHAR to match the migration.
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "last_status", nullable = false, length = 16)
    private ServiceStatus lastStatus = ServiceStatus.UNKNOWN;

    @Column(name = "last_checked_at")
    private Instant lastCheckedAt;

    public MonitoredService(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public void update(String name, String url) {
        if (!this.url.equals(url)) {
            // The previous result says nothing about the new URL.
            this.lastStatus = ServiceStatus.UNKNOWN;
            this.lastCheckedAt = null;
        }
        this.name = name;
        this.url = url;
    }
}
