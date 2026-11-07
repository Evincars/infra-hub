package com.infrahub.watcher.monitoring.persistence;

import java.time.Instant;

import com.infrahub.watcher.monitoring.domain.MonitoredService;
import com.infrahub.watcher.monitoring.domain.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Databázová vrstva pro monitorované služby.
 * <p>
 * Pro juniora: {@code JpaRepository<MonitoredService, Long>} poskytuje například
 * {@code findAll}, {@code findById}, {@code save} a {@code delete}.
 * První generický typ je entita, druhý typ jejího ID. Implementaci rozhraní
 * vytvoří Spring Data za běhu jako bean; nemusíme psát vlastní CRUD třídu.
 * JPA je standard pro mapování Java objektů do relační databáze, zde ho
 * implementuje Hibernate. Repository není samotná databáze.
 */
public interface MonitoredServiceRepository extends JpaRepository<MonitoredService, Long> {

    /**
     * Writes only the check result columns, so a ping finishing in the background never
     * overwrites a concurrent name/URL edit made through the API.
     * <p>
     * Pro juniora: {@code @Query} obsahuje JPQL, proto používá název entity
     * a jejích Java atributů, nikoli SQL názvy tabulky a sloupců.
     * {@code @Modifying} říká Spring Data, že dotaz mění data místo jejich čtení.
     * {@code @Transactional} zajistí transakci pro tento zápis.
     * <p>
     * Pozor na dvě různé věci: Javadoc tag {@code @param} níže pouze dokumentuje
     * parametr a nemění běh programu. Anotace {@code @Param("id")} v podpisu metody
     * naopak propojuje argument {@code id} s pojmenovaným parametrem {@code :id}
     * v dotazu. Velká a malá písmena zde mají význam.
     *
     * @param id ID služby, jejíž výsledek zapisujeme
     * @param status nový výsledek kontroly dostupnosti
     * @param checkedAt okamžik dokončení kontroly
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
