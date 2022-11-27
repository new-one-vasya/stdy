package nw.one.vasya.lesson5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws IOException {
        int count = 0;
        DatagramChannel datagramChannel = DatagramChannel.open().bind(new InetSocketAddress("localhost", 9999));
        datagramChannel.configureBlocking(false);
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        SocketAddress remoteAdd = null;
        while(remoteAdd == null) {
            count++;
            remoteAdd = datagramChannel.receive(buffer);
        }

        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        String msg = new String(bytes);
        System.out.println("Client at #" + remoteAdd + "  sent: " + msg);
        System.out.println("Count # " + count);

    }
}