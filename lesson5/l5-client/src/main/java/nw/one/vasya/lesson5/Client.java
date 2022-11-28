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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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

        Selector selector = Selector.open();
        InetAddress hostIP = InetAddress.getLocalHost();
        InetSocketAddress myAddress =
                new InetSocketAddress(hostIP, port);
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false); //
        datagramChannel.bind(null);
        datagramChannel.register(selector, SelectionKey.OP_WRITE);

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        int cnt = 0;

        while (true) {
            selector.select();
            var selectedKeys = selector.selectedKeys();

            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {
                var key = iter.next();
                if (key.isWritable()) {
                    String str = myList.get(cnt);
                    logger("sending..: " + str);
                    buffer.put(str.getBytes());
                    buffer.flip();
                    datagramChannel.send(buffer, myAddress);
                    buffer.clear();
                    cnt++;
                    key.interestOps(SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    var client = (DatagramChannel) key.channel();
                    client.receive(buffer);
                    System.out.println("Received:... " + new String(buffer.array(), 0, buffer.position()));
                    buffer.clear();
                    key.interestOps(SelectionKey.OP_WRITE);
                }
                iter.remove();
            }
        }

    }

    public static void logger(String msg) {
        System.out.println(msg);
    }
}