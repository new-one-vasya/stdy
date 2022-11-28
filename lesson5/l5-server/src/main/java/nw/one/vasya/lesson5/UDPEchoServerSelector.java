package nw.one.vasya.lesson5;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class UDPEchoServerSelector {

    private static final int TIMEOUT = 3000; // Wait timeout (milliseconds)

    private static final int ECHOMAX = 255; // Maximum size of echo datagram

    public static void main(String[] args) throws IOException {

//        if (args.length != 1) // Test for correct argument list
//            throw new IllegalArgumentException("Parameter(s): <Port>");

        int servPort = 11;
//        int servPort = Integer.parseInt(args[0]);

        // Create a selector to multiplex client connections.
        Selector selector = Selector.open();

        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(new InetSocketAddress(servPort));
        channel.register(selector, SelectionKey.OP_READ, new ClientRecord());

        while (true) { // Run forever, receiving and echoing datagrams
            // Wait for task or until timeout expires
            if (selector.select(TIMEOUT) == 0) {
                System.out.print(".");
                continue;
            }

            // Get iterator on set of keys with I/O to process
            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
            while (keyIter.hasNext()) {
                SelectionKey key = keyIter.next(); // Key is bit mask

                // Client socket channel has pending data?
                if (key.isReadable())
                    handleRead(key);

                // Client socket channel is available for writing and
                // key is valid (i.e., channel not closed).
                if (key.isValid() && key.isWritable())
                    handleWrite(key);

                keyIter.remove();
            }
        }
    }

    public static void handleRead(SelectionKey key) throws IOException {
        DatagramChannel channel = (DatagramChannel) key.channel();
        ClientRecord clntRec = (ClientRecord) key.attachment();
        ByteBuffer buffer = ByteBuffer.allocate(20);
        SocketAddress clientAddress = channel.receive(buffer);
        if (clientAddress != null) {  // Did we receive something?
            // Register write with the selector

            String str = new String(buffer.array(), 0, buffer.position());
            System.out.println("!" + str);
//            channel.send(buffer, clientAddress);
            clntRec.buffer = str;
            clntRec.clientAddress = clientAddress;
            System.out.println("<---");
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    public static void handleWrite(SelectionKey key) throws IOException {
        DatagramChannel channel = (DatagramChannel) key.channel();
        ClientRecord clntRec = (ClientRecord) key.attachment();
        int bytesSent = channel.send(ByteBuffer.wrap(clntRec.buffer.getBytes()), clntRec.clientAddress);
        if (bytesSent != 0) { // Buffer completely written?
            // No longer interested in writes
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    static class ClientRecord {
        public SocketAddress clientAddress;
        public String buffer;
    }
}