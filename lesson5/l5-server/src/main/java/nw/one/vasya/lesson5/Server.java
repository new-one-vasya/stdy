package nw.one.vasya.lesson5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Objects;

public class Server {

    private static final int BUFFER_SIZE = 100;
    static int port = 11;

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        logger("Starting MyDatagramServerExample...");
        InetAddress hostIP = InetAddress.getLocalHost();
        InetSocketAddress address = new InetSocketAddress(hostIP, port);
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false); //
        DatagramSocket datagramSocket = datagramChannel.socket();
        datagramSocket.bind(address);
        datagramChannel.register(selector, SelectionKey.OP_READ);

        SocketAddress addr = null;
        int a = 0;

        // Allocate a byte buffer

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        while (true) {
            selector.select();
            var selectedKeys = selector.selectedKeys();

            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {
                var key = iter.next();
                if (key.isReadable()) {
                    answer(buffer, key);
                }
                iter.remove();
            }
        }

    }

    public static void logger(String msg) {
        System.out.println(msg);
    }


    private static void answer(ByteBuffer buffer, SelectionKey key) throws IOException {
        var client = (DatagramChannel) key.channel();
        SocketAddress address = client.receive(buffer);
        buffer.flip();
        String s = new String(buffer.array(), 0, buffer.position());
        System.out.println("------> " + s);
        client.send(ByteBuffer.wrap(s.getBytes()), address);
        buffer.clear();
    }
}