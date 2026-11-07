package com.infrahub.watcher.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Injectable clock so time-dependent code is deterministic in tests.
 * <p>
 * Pro juniora: bean je objekt, který vytváří a spravuje Spring kontejner.
 * {@code @Configuration} označuje třídu s definicemi beanů; metoda s {@code @Bean}
 * zaregistruje svůj návratový objekt. Výchozí scope je singleton: jeden objekt
 * pro danou definici v kontejneru, ne jeden objekt pro celou JVM.
 * Spring tento {@link Clock} předá do konstruktoru {@code PingService}.
 * V testu lze místo skutečného času předat {@code Clock.fixed(...)}.
 */
@Configuration(proxyBeanMethods = false)
public class ClockConfig {

    /**
     * Vytvoří hodiny používající UTC, aby čas nezávisel na časové zóně serveru.
     *
     * @return hodiny, které Spring zaregistruje jako bean
     */
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
