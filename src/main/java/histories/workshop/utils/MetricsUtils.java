package histories.workshop.utils;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MetricsUtils {
    public static HttpServer startPrometheusScrapeEndpoint(
            PrometheusMeterRegistry registry, int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext(
                    "/metrics",
                    httpExchange -> {
                        String response = registry.scrape();
                        httpExchange.sendResponseHeaders(200, response.getBytes(UTF_8).length);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(UTF_8));
                        }
                    });

            server.start();
            return server;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
