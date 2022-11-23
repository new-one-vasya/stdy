package nw.one.vasya;

import java.nio.channels.SocketChannel;

public class ServerDataEvent {
    public Server server;
    public SocketChannel channel;
    public byte[] data;

    public ServerDataEvent(Server server, SocketChannel channel, byte[] data) {
        this.server = server;
        this.channel = channel;
        this.data = data;
    }

}
