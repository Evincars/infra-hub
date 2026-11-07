package com.infrahub.watcher.monitoring.application;

import java.util.List;

import com.infrahub.watcher.monitoring.domain.MonitoredService;
import com.infrahub.watcher.monitoring.persistence.MonitoredServiceRepository;
import com.infrahub.watcher.shared.web.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use cases for managing the list of monitored services (the transactional boundary for the API).
 * <p>
 * Pro juniora: {@code @Service} zaregistruje tuto aplikační službu jako Spring bean.
 * Lombok {@code @RequiredArgsConstructor} při kompilaci vytvoří konstruktor pro
 * pole {@code final}; Spring přes něj dodá repository (constructor injection).
 * <p>
 * {@code @Transactional} vymezuje databázovou transakci: úspěšné dokončení vede
 * ke commitu, při {@code RuntimeException} nebo {@code Error} je výchozí chování
 * rollback. Checked výjimky standardně automatický rollback nevyvolají.
 * Třídní {@code readOnly = true} je výchozí nastavení pro čtení a optimalizační
 * pokyn, ne spolehlivý zákaz zápisů. Metody pro zápis ho přepisují svou anotací.
 * <p>
 * Spring transakce zde zapíná přes proxy: volání musí přijít přes Spring bean,
 * například z controlleru. Volání vlastní metody přes {@code this} neprojde
 * proxy, takže samo nespustí novou transakci ani nepřepne její nastavení.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonitoredServiceManager {

    private static final String RESOURCE = "MonitoredService";

    private final MonitoredServiceRepository repository;

    public List<MonitoredService> findAll() {
        return repository.findAll(Sort.by("name").and(Sort.by("id")));
    }

    public MonitoredService get(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
    }

    /**
     * Zaregistruje službu a uloží ji do databáze.
     *
     * @param name zobrazované jméno služby; validaci API provádí request DTO
     * @param url HTTP(S) adresa, kterou má pinger kontrolovat
     * @return uložená entita včetně databází přiděleného ID
     */
    @Transactional
    public MonitoredService create(String name, String url) {
        return repository.save(new MonitoredService(name, url));
    }

    /**
     * Změní jméno a URL existující služby.
     * <p>
     * Entita načtená uvnitř transakce je spravovaná Hibernate. Dirty checking
     * rozpozná změny jejích atributů a při flush je odešle do databáze; proto zde
     * není nutné volat {@code save} znovu. Při změně URL entita resetuje stav.
     *
     * @param id ID existující služby
     * @param name nové zobrazované jméno
     * @param url nová kontrolovaná adresa
     * @return změněná entita
     * @throws ResourceNotFoundException pokud služba s tímto ID neexistuje
     */
    @Transactional
    public MonitoredService update(Long id, String name, String url) {
        MonitoredService service = get(id);
        service.update(name, url); // flushed by dirty checking on commit
        return service;
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(get(id));
    }
}
