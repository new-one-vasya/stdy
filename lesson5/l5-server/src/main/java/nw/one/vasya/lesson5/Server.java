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
import java.util.Objects;

public class Server {

    private static final int BUFFER_SIZE = 1024;
    static int port = 11;

    public static void main(String[] args) throws IOException {
        logger("Starting MyDatagramServerExample...");
        InetAddress hostIP = InetAddress.getLocalHost();
        InetSocketAddress address = new InetSocketAddress(hostIP, port);
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false); //
        DatagramSocket datagramSocket = datagramChannel.socket();
        datagramSocket.bind(address);

        SocketAddress addr = null;
        int a = 0;

        // Allocate a byte buffer
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        while (true) {
            do {
                addr = datagramChannel.receive(buffer);
            } while (addr == null);
            buffer.flip();
            System.out.print("\nFrom " + addr + " Data...: ");
//            while (buffer.hasRemaining()) {
                System.out.write(buffer.get());
//            }
            buffer.clear();
        }
    }

    public static void logger(String msg) {
        System.out.println(msg);
    }
}