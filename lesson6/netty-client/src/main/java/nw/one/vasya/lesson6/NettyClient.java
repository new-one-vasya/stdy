package nw.one.vasya.lesson6;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import nw.one.vasya.lesson6.handler.ClientHandler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NettyClient {

    private static final List<String> COMMANDS = new ArrayList<>(List.of("5:hello", "4:cool", "6:haha"));

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 8080;
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(
                            new LoggingHandler(LogLevel.INFO),
                            new StringDecoder(StandardCharsets.UTF_8),
                            new StringEncoder(StandardCharsets.UTF_8),
                            new ClientHandler()
                    );
                }
            });

            ChannelFuture lastWriteFuture = null;
            Channel channel = bootstrap.connect(host, port).sync().channel();

            for (String line : COMMANDS) {
                lastWriteFuture = channel.writeAndFlush(line);
                Thread.sleep(100); // Моё решение склеивания запросов в один пакет
            }
            channel.closeFuture().sync();

            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }

        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}