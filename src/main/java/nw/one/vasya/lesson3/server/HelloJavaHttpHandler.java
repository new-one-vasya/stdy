package nw.one.vasya.lesson3.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/*
nc -l 8080
curl http://localhost:8080/ -d name=myname -d age=26 -d login=java
 */
public class HelloJavaHttpHandler implements HttpHandler {


    private static final Logger LOGGER = LoggerFactory.getLogger(HelloJavaHttpHandler.class);
    private static final String HELLO_JAVA_MESSAGE = "Hello Java";
    private static final String RIGHT_PASSWORD = "java";
    private static final String LOGIN_PARAM = "login";
    private static final int SUCCESS = 200;
    private static final int BAD_REQUEST = 400;

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        LOGGER.debug("Handle request");
        if (exchange.getRequestMethod().equals("POST")) {
            doWork(exchange);
        } else {
            LOGGER.debug("Unsupported request method {}", exchange.getRequestMethod());
            throw new UnsupportedOperationException("Поддерживаются только POST запросы");
        }
    }

    private void doWork(final HttpExchange exchange) throws IOException {

        MultiMap<String> params = new MultiMap<>();

        try (var request = exchange.getRequestBody()) {
            StringWriter writer = new StringWriter();
            IO.copy(new InputStreamReader(request), writer);
            String content = writer.toString();
            LOGGER.debug("Request content: {}", content);
            UrlEncoded.decodeTo(content, params, StandardCharsets.UTF_8);
        }

        if (RIGHT_PASSWORD.equals(params.getValue(LOGIN_PARAM, 0))) {
            try (var response = exchange.getResponseBody()) {
                LOGGER.debug("You are right!");
                exchange.sendResponseHeaders(SUCCESS, HELLO_JAVA_MESSAGE.length());
                response.write(HELLO_JAVA_MESSAGE.getBytes(StandardCharsets.UTF_8));
                response.flush();
            }
        } else {
            LOGGER.debug("Wrong password: {}", params.getValue(LOGIN_PARAM, 0));
            exchange.sendResponseHeaders(BAD_REQUEST, -1);
            throw new InvalidLoginException();
        }
    }
}
