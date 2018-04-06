package com.luangeng.starfish.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by LG on 2017/9/28.
 */
public class RpcServer extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private static boolean start = false;

    private static RpcServer server = new RpcServer();

    private int port;

    private RpcServer() {
    }

    public static RpcServer getServer() {
        return server;
    }

    public void startUp(int port) {
        if (start) {
            LOGGER.info("Server already startup.");
            return;
        }
        server.port = port;
        server.start();
    }

    private void bind() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG, 128);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childHandler(new MyChannelInitializer());

            ChannelFuture future = bootstrap.bind(port).sync();
            LOGGER.info("server started on port:" + port);
            start = true;
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void shutDown() {
        start = false;
    }

    @Override
    public void run() {
        bind();
    }

    private static class MyChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel channel) throws Exception {
            channel.pipeline()
                    .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4))
                    .addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())))
                    .addLast(new LengthFieldPrepender(4))
                    .addLast(new ObjectEncoder())
                    .addLast(new ServerHandler());
        }
    }

}
