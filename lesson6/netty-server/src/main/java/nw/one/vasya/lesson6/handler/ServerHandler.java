package nw.one.vasya.lesson6.handler;

import io.netty.channel.*;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ServerHandler.class);

    static final List<Channel> channels = new ArrayList<>();
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        LOGGER.info("Connection for client - ", ctx);
        channels.add(ctx.channel());
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        LOGGER.info("Message received: {}", msg);
        for (Channel c : channels) {
            replay(c, msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.info("Closing connection for client - ", ctx);
        ctx.close();
    }

    public void replay(Channel channel, String msg) {
        if (isValid(msg)) {
            send(channel, Answer.WIN.getAnswer());
        } else {
            ChannelFuture send = send(channel, Answer.LOOSE.getAnswer());
            send.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private boolean isValid(String msg) {
        if (msg.split(":").length != 2) {
            throw new IllegalArgumentException("Строка должна содержать :");
        }
        String[] split = msg.split(":", 2);
        return Integer.parseInt(split[0]) == split[1].length();
    }

    private ChannelFuture send(Channel channel, String answer) {
        return channel.write(answer);
    }

    private enum Answer {
        WIN("2:ok"),
        LOOSE("3:err");

        final String answer;
        Answer(String answer) {
            this.answer = answer;
        }

        public String getAnswer() {
            return answer;
        }
    }
}