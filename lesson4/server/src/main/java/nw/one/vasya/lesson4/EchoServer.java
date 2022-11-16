package nw.one.vasya.lesson4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class EchoServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EchoServer.class);

    public static void main(String[] args) throws IOException {
        byte[] buff = new byte[280]; // twitter size
        try (DatagramSocket socket = new DatagramSocket(9000)) {

            DatagramPacket packet = new DatagramPacket(buff, buff.length);

            LOGGER.debug("Up and run");

            while(true) {
                socket.receive(packet);

                var address = packet.getAddress();
                var port = packet.getPort();

                String message = new String(packet.getData(), 0, packet.getLength());
                LOGGER.info("Received message: {}", message);

                if ("stop".equals(message)) {
                    break;
                }

                LOGGER.info("Send back: {}", message);
                packet = new DatagramPacket(buff, buff.length, address, port);
                socket.send(packet);
            }

            LOGGER.debug("exit");
        }
    }
}