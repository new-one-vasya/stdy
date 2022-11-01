package nw.one.vasya.lesson2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;

/**
 * Главный класс второй нелели.
 */
public final class Main {

    /**
     * Огранчиение на вывод текста, дабы не пугать себя и консоль.
     */
    public static final int REDUCE_INDEX = 100;

    /**
     * Закрытый потому что утилитарный.
     */
    private Main() {
    }

    /**
     * main метод.
     * @param args в первом агруменет должен быть адрес
     * @exception IOException пришло от {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler)} или {@link URL#URL(String)}
     * @exception InterruptedException пришло от {@link HttpClient#send(HttpRequest, HttpResponse.BodyHandler)}
     */
    public static void main(final String[] args) throws IOException, InterruptedException {

        if (args.length != 1) {
            throw new RuntimeException("И где адрес?");
        }

        callNew(args[0]);
        callOld(args[0]);

    }

    private static void callOld(final String location) throws IOException {
        Objects.requireNonNull(location);

        URL address = new URL(location);
        URLConnection conn = address.openConnection();

        System.out.println("Size = " + conn.getContentLength());
        System.out.println("Type = " + conn.getContentType());

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            int count = 0;
            while ((line = bufferedReader.readLine()) != null && count < REDUCE_INDEX) {
                System.out.println(line.length() < REDUCE_INDEX ? line : line.substring(0, REDUCE_INDEX));
                count += line.length();
            }
        }

    }

    private static void callNew(final String location) throws IOException, InterruptedException {
        Objects.requireNonNull(location);

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(location)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Size = " + response.headers().firstValue("content-length").orElse("-1"));
        System.out.println("Type = " + response.headers().firstValue("content-type").orElse("unknown"));
        System.out.println("Content" + Optional.of(response.body()).map(val -> val.substring(0, REDUCE_INDEX)).orElse("empty"));
    }
}
