package nw.one.vasya.lesson5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private static boolean NEED_SAND_STOP = true;

    public static void main(String[] args) throws IOException {

        if (args.length != 1){
            LOGGER.warn("Где хост?");
            return;
        }

        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(args[0], 9999));

        Selector selector = Selector.open();
        SelectionKey key = channel.register(selector, SelectionKey.OP_WRITE);

        total:
        while (true) {
            int select = selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                if (next.isWritable()) {

                    DatagramChannel localChannel = (DatagramChannel) next.channel();
                    localChannel.write(ByteBuffer.wrap("Hello".getBytes()));

                    System.out.println("To write: hello");
//                    key.interestOps(SelectionKey.OP_READ);
                    iterator.remove();
                } else if (next.isReadable()){

                    DatagramChannel localChannel = (DatagramChannel) next.channel();
                    ByteBuffer bb = ByteBuffer.allocate(16);
                    localChannel.read(bb);
                    bb.flip();
                    System.out.println(new String(bb.array(), 0, bb.limit()));
                    iterator.remove();
                    break total;
                }
            }
        }


        selector.close();
        channel.close();

//        --------------------------------------------------------

//        InetAddress address = InetAddress.getByName("localhost");
//        try (DatagramSocket socket = new DatagramSocket()) {
//            byte[] buff;
//
//            for(String cmd : args) {
//                buff = cmd.toLowerCase().getBytes();
//                DatagramPacket packet = new DatagramPacket(buff, buff.length, address, 9000);
//                socket.send(packet);
//
//                LOGGER.debug("Client send: {}", cmd);
//
//                if ("stop".equalsIgnoreCase(cmd)) {
//                    NEED_SAND_STOP = false;
//                    break;
//                }
//
//                packet = new DatagramPacket(buff, buff.length);
//                socket.receive(packet);
//                LOGGER.debug("Received {}", new String(packet.getData()));
//            }
//
//            if (NEED_SAND_STOP) {
//                LOGGER.debug("Forced STOP");
//                buff = "stop".getBytes();
//                DatagramPacket packet = new DatagramPacket(buff, buff.length, address, 9000);
//                socket.send(packet);
//            }
//
//            LOGGER.debug("exit");
//        }
    }
}