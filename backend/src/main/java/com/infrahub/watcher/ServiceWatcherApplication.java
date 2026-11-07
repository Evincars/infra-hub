package com.infrahub.watcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Vstupní bod backendu.
 * <p>
 * Pro juniora: {@code @SpringBootApplication} spojuje konfiguraci, automatickou
 * konfiguraci Spring Boot a vyhledávání komponent v tomto balíčku a podbalíčcích.
 * Díky tomu Spring najde například naše {@code @Service} a {@code @RestController}.
 * {@code @ConfigurationPropertiesScan} vyhledává třídy s typovaným nastavením,
 * zde například {@code PingProperties}.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ServiceWatcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceWatcherApplication.class, args);
    }
}
