package nw.one.vasya.lesson4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ReceiverProgram {

    public static DatagramChannel startReceiver() throws IOException {
        DatagramChannel receiver = DatagramChannel.open();
        // Listening SocketAddress
        InetSocketAddress address = new InetSocketAddress("localhost", 9999);
        receiver.bind(address); // The receiver is listening at localhost:9999

        System.out.println("Receiver started at #" + address);
        return receiver;
    }

    public static String receiveMessage(DatagramChannel receiver) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketAddress senderAddress = receiver.receive(buffer);
        String message = extractMessage(buffer);
        System.out.println("Received message from sender: " + senderAddress);
        return message;
    }

    private static String extractMessage(ByteBuffer buffer) {
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        String msg = new String(bytes);
        return msg;
    }

    public static void main(String[] args) throws IOException {
        DatagramChannel receiver = startReceiver();
        while (true) {
            String message = receiveMessage(receiver);
            System.out.println(" - Message: " + message);
            if("Bye!".equals(message)) {
                break;
            }
        }
        receiver.close();
        System.out.println("Receiver closed!");
    }
}
