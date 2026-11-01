package com.infrahub.watcher.monitoring.ping;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.http.HttpClient;
import java.time.Duration;

import com.infrahub.watcher.monitoring.domain.ServiceStatus;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Exercises the probe against a real in-process HTTP server. */
class HttpProbeTest {

    private HttpServer server;
    private HttpClient httpClient;
    private HttpProbe probe;
    private String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/ok", exchange -> respond(exchange, 200));
        server.createContext("/redirect", exchange -> {
            exchange.getResponseHeaders().add("Location", "/ok");
            respond(exchange, 302);
        });
        server.createContext("/error", exchange -> respond(exchange, 503));
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();

        PingProperties properties = new PingProperties(Duration.ofSeconds(2), Duration.ofSeconds(2), 4);
        httpClient = new PingConfiguration().pingHttpClient(properties);
        probe = new HttpProbe(httpClient, properties);
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
        httpClient.close();
    }

    @Test
    void successfulResponseIsUp() throws Exception {
        assertThat(probe.probe(baseUrl + "/ok")).isEqualTo(ServiceStatus.UP);
    }

    @Test
    void redirectsAreFollowed() throws Exception {
        assertThat(probe.probe(baseUrl + "/redirect")).isEqualTo(ServiceStatus.UP);
    }

    @Test
    void serverErrorIsDown() throws Exception {
        assertThat(probe.probe(baseUrl + "/error")).isEqualTo(ServiceStatus.DOWN);
    }

    @Test
    void unreachableHostIsDown() throws Exception {
        int closedPort;
        try (ServerSocket socket = new ServerSocket(0)) {
            closedPort = socket.getLocalPort();
        }
        assertThat(probe.probe("http://127.0.0.1:" + closedPort)).isEqualTo(ServiceStatus.DOWN);
    }

    @Test
    void malformedUrlIsDown() throws Exception {
        assertThat(probe.probe("not a url")).isEqualTo(ServiceStatus.DOWN);
    }

    private static void respond(com.sun.net.httpserver.HttpExchange exchange, int status) throws java.io.IOException {
        exchange.sendResponseHeaders(status, -1);
        exchange.close();
    }
}
