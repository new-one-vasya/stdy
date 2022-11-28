package nw.one.vasya.lesson5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private static final String STOP = "stop";
    private static final int PROT = 9999;
    private static final int BUFFER_SIZE = 10;
    private static Queue<String> COMMANDS = new LinkedList<>();

    static {
        COMMANDS.add("Hello");
        COMMANDS.add("Stop");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 1){
            LOGGER.error("Где хост?");
            return;
        }

        Selector selector = Selector.open();
        InetAddress hostIP = InetAddress.getByName(args[0]);
        InetSocketAddress myAddress =
                new InetSocketAddress(hostIP, PROT);
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false); //
        datagramChannel.bind(null);
        datagramChannel.register(selector, SelectionKey.OP_WRITE);

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        while (true) {
            selector.select();
            var selectedKeys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                var key = iterator.next();
                if (key.isWritable()) {
                    String str = COMMANDS.poll();
                    LOGGER.debug("sending: {}", str);
                    datagramChannel.send(ByteBuffer.wrap(str.getBytes()), myAddress);
                    if (STOP.equalsIgnoreCase(str)) {
                        System.exit(0); // тут этому самое место
                    }
                    key.interestOps(SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    var client = (DatagramChannel) key.channel();
                    client.receive(buffer);
                    LOGGER.debug("received: {}", new String(buffer.array(), 0, buffer.position()));
                    buffer.clear();
                    key.interestOps(SelectionKey.OP_WRITE);
                }
                iterator.remove();
            }
        }
    }
}