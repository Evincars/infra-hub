package com.infrahub.watcher.monitoring.application;

import java.util.List;

import com.infrahub.watcher.monitoring.domain.MonitoredService;
import com.infrahub.watcher.monitoring.persistence.MonitoredServiceRepository;
import com.infrahub.watcher.shared.web.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Use cases for managing the list of monitored services (the transactional boundary for the API). */
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

    @Transactional
    public MonitoredService create(String name, String url) {
        return repository.save(new MonitoredService(name, url));
    }

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
