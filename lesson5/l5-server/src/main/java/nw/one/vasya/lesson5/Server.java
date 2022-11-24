package nw.one.vasya.lesson5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws IOException {

        byte[] buff = new byte[16]; // twitter size

        DatagramChannel channel = DatagramChannel.open();

        channel.socket().bind(new InetSocketAddress( 9999));
        channel.configureBlocking(false);

        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);

        total:
        while (true) {
            int select = selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();

                if (next.isReadable()) {
                    if (!next.isValid()) {
                        continue;
                    }
                    System.out.println("Get some request");

                    DatagramChannel localChannel = (DatagramChannel) next.channel();

                    ByteBuffer allocate = ByteBuffer.allocate(16);
                    try {
                        localChannel.read(allocate);
                    } catch (NotYetConnectedException e) {
                        System.out.println("NotYetConnectedException");
                        iterator.remove();
                        continue;
                    }
                    allocate.flip();
                    System.out.println(new String(allocate.array()));
                    next.interestOps(SelectionKey.OP_WRITE);
                    iterator.remove();
                } else if (next.isWritable()) {

                    DatagramChannel localChannel = (DatagramChannel) next.channel();
                    localChannel.write(ByteBuffer.wrap("hello".getBytes()));
                    iterator.remove();
                    break total;
                }

                iterator.remove();
            }
        }





        selector.close();
        channel.close();

//        --------------------------------------------------------
//
//        try (DatagramSocket socket = new DatagramSocket(9000)) {
//
//            DatagramPacket packet = new DatagramPacket(buff, buff.length);
//
//            LOGGER.debug("Up and run");
//
//            while(true) {
//                socket.receive(packet);
//
//                var address = packet.getAddress();
//                var port = packet.getPort();
//
//                String message = new String(packet.getData(), 0, packet.getLength());
//                LOGGER.info("Received message: {}", message);
//
//                if ("stop".equals(message)) {
//                    break;
//                }
//
//                LOGGER.info("Send back: {}", message);
//                packet = new DatagramPacket(buff, buff.length, address, port);
//                socket.send(packet);
//            }
//
//            LOGGER.debug("exit");
//        }
    }
}