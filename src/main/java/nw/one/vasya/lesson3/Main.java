package nw.one.vasya.lesson3;

import com.sun.net.httpserver.HttpServer;
import nw.one.vasya.lesson3.server.HelloJavaHttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Главный.
 */
public final class Main {

    private Main() {
    }

    private static final String LOCALHOST_ADDRESS = "http://localhost:8080";

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * Главный.
     * @param args аргументы не используются
     * @throws IOException исключение
     */
    public static void main(final String[] args) throws IOException, InterruptedException, ExecutionException {
        runServer();
        sendRequests();
    }

    private static void sendRequests() throws IOException, InterruptedException, ExecutionException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(LOCALHOST_ADDRESS))
                                         .POST(HttpRequest.BodyPublishers.ofString("login=jav&name=myName"))
                                         .build();

        var future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                                .thenAccept(Main::parseResponse);
        LOGGER.debug("future - 1 - {}", future);
        future.get();
    }

    private static void runServer() throws IOException {
        final var server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.createContext("/", new HelloJavaHttpHandler());
        server.start();
        LOGGER.debug("Now started!");

    }

    private static void parseResponse(final HttpResponse<String> response) {
        LOGGER.info("Response from: {}", response.uri());
        LOGGER.info("Response statusCode: {}", response.statusCode());
        LOGGER.info("Response body: {}", response.body());
    }
}
