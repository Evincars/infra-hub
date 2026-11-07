package com.infrahub.watcher.monitoring.ping;

import java.net.http.HttpClient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration(proxyBeanMethods = false)
public class PingConfiguration {

    /**
     * HttpClient is AutoCloseable (Java 21+), so Spring closes it on shutdown.
     * <p>
     * Pro juniora: argument metody {@code @Bean} dodá Spring z kontejneru,
     * nemusíme tedy ručně vytvářet {@code PingProperties}.
     *
     * @param properties nastavení pingeru navázané na konfiguraci aplikace
     * @return sdílený HTTP klient s nastaveným timeoutem a přesměrováním
     */
    @Bean
    HttpClient pingHttpClient(PingProperties properties) {
        return HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Set {@code watcher.ping.enabled=false} to turn off the pinger (e.g. in tests or API-only instances).
     * <p>
     * {@code @EnableScheduling} zapíná zpracování metod s {@code @Scheduled}.
     * {@code @ConditionalOnProperty} tuto konfiguraci aktivuje jen podle nastavení;
     * zde je plánování ve výchozím stavu zapnuté i při chybějící hodnotě.
     */
    @Configuration(proxyBeanMethods = false)
    @EnableScheduling
    @ConditionalOnProperty(prefix = "watcher.ping", name = "enabled", havingValue = "true", matchIfMissing = true)
    static class SchedulingConfiguration {
    }
}
