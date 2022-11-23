package nw.one.vasya;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class EchoWorker implements Runnable {

    private final List<ServerDataEvent> queue = new LinkedList<>();

    public void processData(Server server, SocketChannel channel, byte[] array, int count) {
        byte[] data = new byte[count];
        System.arraycopy(array, 0, data, 0, count);
        synchronized (queue) {
            queue.add(new ServerDataEvent(server, channel, data));
            queue.notify();
        }
    }

    @Override
    public void run() {
        ServerDataEvent event;
        while (true) {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Received(s) = " + new String(queue.get(0).data));
                event = queue.remove(0);
            }
            event.server.send(event.channel, event.data);
        }
    }
}
