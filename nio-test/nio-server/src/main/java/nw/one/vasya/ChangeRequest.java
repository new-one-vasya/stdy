package nw.one.vasya;

import java.nio.channels.SocketChannel;

public class ChangeRequest {
    public OpType type;
    public SocketChannel channel;
    public int ops;

    public ChangeRequest(SocketChannel channel, OpType opType, int opWrite) {
        this.channel = channel;
        this.type = opType;
        this.ops = opWrite;
    }
}
