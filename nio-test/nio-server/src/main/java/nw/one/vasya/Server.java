package nw.one.vasya;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static nw.one.vasya.OpType.CHANGEOPS;

public class Server {

    private static int PORT = 9000;
    private static String ADDRESS = "localhost";
    private Selector selector;
    private ByteBuffer buffer = ByteBuffer.allocate(8192);
    private EchoWorker worker = new EchoWorker();
    private final List<ChangeRequest> chRq = new LinkedList<>();
    private final Map<SocketChannel, List<ByteBuffer>> pending = new ConcurrentHashMap<>();

    private Server() throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(ADDRESS, PORT));
        selector = SelectorProvider.provider().openSelector();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        new Thread(worker).run();
    }


    public static void main(String[] args) throws IOException {
        new Server().run();
    }

    private void run() throws IOException {
        while (true) {
            synchronized (chRq) {
                for (ChangeRequest rq : chRq) {
                    switch (rq.type) {
                        case CHANGEOPS:
                            SelectionKey key = rq.channel.keyFor(selector);
                            key.interestOps(rq.ops);
                            break;
                        default:
                    }
                }
                chRq.clear();
            }
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    accept(key);
                }
                if (key.isReadable()) {
                    read(key);
                }
                if (key.isWritable()) {
                    write(key);
                }
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        buffer.clear();
        int readNum = socketChannel.read(buffer);
        worker.processData(this, socketChannel, buffer.array(), readNum);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        synchronized (pending) {
            List<ByteBuffer> queue = pending.get(socketChannel);
            while (!queue.isEmpty()){
                ByteBuffer buf = queue.get(0);
                socketChannel.write(buf);
                if (buf.remaining() > 0) {
                    break;
                }
                System.out.println("Echo send " + new String(buf.array()));
                queue.remove(0);
            }
            if (queue.isEmpty()) {
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    public void send(SocketChannel channel, byte[] data) {
        synchronized (chRq) {
            chRq.add(new ChangeRequest(channel, CHANGEOPS, SelectionKey.OP_WRITE));
            synchronized (pending) {
                List<ByteBuffer> queue = pending.get(channel);
                if (queue == null) {
                    queue = new ArrayList<>();
                    pending.put(channel, queue);
                }
                queue.add(ByteBuffer.wrap(data));
            }
        }
        selector.wakeup();
    }
}