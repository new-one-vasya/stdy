package nw.one.vasya.lesson5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {
    private static final int BUFFER_SIZE = 1024;
    static int port = 11;
    private static List<String> myList = new ArrayList<String>(
            Arrays.asList("Tyrion Lannister",
                    "Cersei Lannister",
                    "Daenerys Targaryen",
                    "Jon Snow",
                    "Sansa Stark",
                    "Arya Stark",
                    "Jaime Lannister",
                    "Jorah Mormont",
                    "Lord Varys"));

    public static void main(String[] args) throws IOException {
        logger("Starting MyDatagramClientExample...");

        InetAddress hostIP = InetAddress.getLocalHost();
        InetSocketAddress myAddress =
                new InetSocketAddress(hostIP, port);
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false); //
        datagramChannel.bind(null);

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        for (String cast: myList) {
            logger("sending..: " + cast);
            buffer.put(cast.getBytes());
            buffer.flip();
            datagramChannel.send(buffer, myAddress);
            buffer.clear();
        }
    }

    public static void logger(String msg) {
        System.out.println(msg);
    }
}