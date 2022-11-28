package nw.one.vasya.lesson5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static final int PORT = 9999;
    private static final String STOP = "stop";

    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();

        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(new InetSocketAddress(PORT));
        channel.register(selector, SelectionKey.OP_READ, new Payload());

        LOGGER.info("Up and run");
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isReadable()) {
                    handleRead(key);
                } else if (key.isValid() && key.isWritable()) {
                    handleWrite(key);
                }
                iterator.remove();
            }
        }
    }

    public static void handleRead(SelectionKey key) throws IOException {
        DatagramChannel channel = (DatagramChannel) key.channel();
        Payload payload = (Payload) key.attachment();
        ByteBuffer buffer = ByteBuffer.allocate(20);
        SocketAddress clientAddress = channel.receive(buffer);
        if (clientAddress != null) { // без этого хука не работет
            String str = new String(buffer.array(), 0, buffer.position());
            LOGGER.debug("Received: {}", str);
            if (STOP.equalsIgnoreCase(str)) {
                System.exit(0); // тут этому самое место
            }
            payload.data = str;
            payload.clientAddress = clientAddress;
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    public static void handleWrite(SelectionKey key) throws IOException {
        DatagramChannel channel = (DatagramChannel) key.channel();
        Payload payload = (Payload) key.attachment();
        int bytesSent = channel.send(ByteBuffer.wrap(payload.data.getBytes()), payload.clientAddress);
        if (bytesSent != 0) {
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    static class Payload {
        public SocketAddress clientAddress;
        public String data;
    }
}