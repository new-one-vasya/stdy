package nw.one.vasya.lesson5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private static boolean NEED_SAND_STOP = true;

    public static void main(String[] args) throws IOException {

        if (args.length != 1){
            LOGGER.warn("Где хост?");
            return;
        }

        DatagramChannel datagramChannel = DatagramChannel.open().bind(null);
        datagramChannel.configureBlocking(false);

        String msg = "hello";
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 9999);

        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
        datagramChannel.send(buffer, serverAddress);

    }
}