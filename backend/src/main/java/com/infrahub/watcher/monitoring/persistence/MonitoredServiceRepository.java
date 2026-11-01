package com.infrahub.watcher.monitoring.persistence;

import java.time.Instant;

import com.infrahub.watcher.monitoring.domain.MonitoredService;
import com.infrahub.watcher.monitoring.domain.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MonitoredServiceRepository extends JpaRepository<MonitoredService, Long> {

    /**
     * Writes only the check result columns, so a ping finishing in the background never
     * overwrites a concurrent name/URL edit made through the API.
     *
     * @return number of rows updated (0 if the service was deleted mid-check)
     */
    @Transactional
    @Modifying
    @Query("""
            update MonitoredService s
               set s.lastStatus = :status, s.lastCheckedAt = :checkedAt
             where s.id = :id
            """)
    int updateStatus(@Param("id") Long id,
                     @Param("status") ServiceStatus status,
                     @Param("checkedAt") Instant checkedAt);
}
