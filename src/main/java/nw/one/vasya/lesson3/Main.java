package nw.one.vasya.lesson3;

import com.sun.net.httpserver.HttpServer;
import nw.one.vasya.lesson3.server.HelloJavaHttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * Главный.
 */
//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//        executorService.schedule(() -> {server.stop(1); LOGGER.debug("Now stopped!!!");}, 1, TimeUnit.SECONDS);
public final class Main {

    private Main() {
    }

//    static {
//        LogManager.getLogManager().reset();
//        SLF4JBridgeHandler.install();
//    }

    private static final String LOCALHOST_ADDRESS = "http://localhost:8080";

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * Главный.
     * @param args аргументы не используются
     * @throws IOException исключение
     */
    public static void main(final String[] args) throws IOException, InterruptedException, ExecutionException {
//        SLF4JBridgeHandler.removeHandlersForRootLogger();
//        SLF4JBridgeHandler.install();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        java.util.logging.Logger.getLogger("").setLevel(Level.FINEST);



        var server = runServer();
        sendRequests();
//        Runtime.getRuntime().exit(1);
        Thread.sleep(5000);
        server.stop(1);

        System.out.println(Thread.activeCount());
        printThreadGroupInfo(Thread.currentThread().getThreadGroup());

//        Thread.currentThread().getThreadGroup().interrupt();
//
//        System.out.println(Thread.activeCount());
//        printThreadGroupInfo(Thread.currentThread().getThreadGroup());
    }

    private static void printThreadGroupInfo(ThreadGroup threadGroup) {
        if (threadGroup == null) {
            return;
        }
        threadGroup.list();
        printThreadGroupInfo(threadGroup.getParent());
    }

    private static void sendRequests() throws IOException, InterruptedException, ExecutionException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(LOCALHOST_ADDRESS))
                                         .POST(HttpRequest.BodyPublishers.ofString("login=java&name=myName"))
                                         .build();

        var future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                                .thenAccept(Main::parseResponse);
        LOGGER.debug("future - 1 - {}", future);
        future.get();
    }

    private static HttpServer runServer() throws IOException {
        final var server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
//        server.setExecutor(Executors.newSingleThreadExecutor());
        server.createContext("/", new HelloJavaHttpHandler());
        server.start();
        LOGGER.debug("Now started!");
        return server;
    }

    private static void parseResponse(final HttpResponse<String> response) {
        LOGGER.info("Response from: {}", response.uri());
        LOGGER.info("Response statusCode: {}", response.statusCode());
        LOGGER.info("Response body: {}", response.body());
    }
}
