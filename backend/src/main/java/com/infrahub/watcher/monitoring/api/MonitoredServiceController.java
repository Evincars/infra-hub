package com.infrahub.watcher.monitoring.api;

import java.net.URI;
import java.util.List;

import com.infrahub.watcher.monitoring.application.MonitoredServiceManager;
import com.infrahub.watcher.monitoring.domain.MonitoredService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * HTTP vstup do aplikace; databázovou práci deleguje na manager.
 * <p>
 * Pro juniora: {@code @RestController} je Spring bean, jehož návratové hodnoty
 * Spring zapisuje do těla HTTP odpovědi (zde jako JSON).
 * {@code @RequestMapping} určuje společný prefix URL a {@code @GetMapping},
 * {@code @PostMapping} a další anotace volí HTTP metodu a cestu.
 * Response DTO odděluje veřejný API formát od databázové entity.
 */
@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class MonitoredServiceController {

    private final MonitoredServiceManager manager;

    @GetMapping
    public List<MonitoredServiceResponse> list() {
        return manager.findAll().stream().map(MonitoredServiceResponse::from).toList();
    }

    /**
     * Vrátí jednu službu.
     * <p>
     * {@code @PathVariable} převede část URL, například {@code /42}, na argument
     * {@code id}. Javadoc {@code @param} je jen jeho popis, nikoli anotace.
     *
     * @param id ID služby z URL
     * @return veřejná reprezentace služby
     */
    @GetMapping("/{id}")
    public MonitoredServiceResponse get(@PathVariable Long id) {
        return MonitoredServiceResponse.from(manager.get(id));
    }

    /**
     * Přijme JSON a zaregistruje novou službu.
     * <p>
     * {@code @RequestBody} převádí tělo požadavku na request DTO;
     * {@code @Valid} spouští Bean Validation podle omezení na tomto DTO.
     * Neplatný vstup Spring odmítne dříve, než se zavolá manager.
     *
     * @param request jméno a URL z JSON požadavku
     * @return HTTP 201 s tělem odpovědi a hlavičkou Location nové služby
     */
    @PostMapping
    public ResponseEntity<MonitoredServiceResponse> create(@Valid @RequestBody MonitoredServiceRequest request) {
        MonitoredService created = manager.create(request.name(), request.url());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(MonitoredServiceResponse.from(created));
    }

    @PutMapping("/{id}")
    public MonitoredServiceResponse update(@PathVariable Long id, @Valid @RequestBody MonitoredServiceRequest request) {
        return MonitoredServiceResponse.from(manager.update(id, request.name(), request.url()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        manager.delete(id);
        return ResponseEntity.noContent().build();
    }
}
