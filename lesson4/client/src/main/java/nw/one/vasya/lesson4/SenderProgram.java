package nw.one.vasya.lesson4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class SenderProgram {
    public static DatagramChannel startSender() throws IOException {
        DatagramChannel sender = DatagramChannel.open();
        // SocketAddress
        SocketAddress address = null;
        sender.bind(address);

        sender.configureBlocking(false);
        return sender;
    }
    public static void sendMessage(DatagramChannel sender, String msg, //
                                   SocketAddress receiverAddress) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
        sender.send(buffer, receiverAddress);
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramChannel sender = startSender();

        String[] messages = new String[] { "Hello", "How are you?", "Bye!" };
        // SocketAddress of the Receiver.
        InetSocketAddress receiverAddress = new InetSocketAddress("localhost", 9999);

        for (String message : messages) {
            // Send message to the Receiver!
            sendMessage(sender, message, receiverAddress);
            Thread.sleep(2 * 1000); // 2 seconds.
        }
        sender.close();
    }
}
