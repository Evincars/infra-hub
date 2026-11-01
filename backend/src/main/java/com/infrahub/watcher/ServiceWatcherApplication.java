package com.infrahub.watcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ServiceWatcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceWatcherApplication.class, args);
    }
}
