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

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class MonitoredServiceController {

    private final MonitoredServiceManager manager;

    @GetMapping
    public List<MonitoredServiceResponse> list() {
        return manager.findAll().stream().map(MonitoredServiceResponse::from).toList();
    }

    @GetMapping("/{id}")
    public MonitoredServiceResponse get(@PathVariable Long id) {
        return MonitoredServiceResponse.from(manager.get(id));
    }

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
