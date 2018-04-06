package com.luangeng.starfish.client;

import com.luangeng.starfish.common.RpcRequest;
import com.luangeng.starfish.common.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 一个连接的封装
 */
public class RpcClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    //已连接主机的缓存
    private static Map<String, RpcClient> clientMap = new HashMap<String, RpcClient>();

    private Channel channel;

    private EventLoopGroup group;

    private String addr;

    private RpcClient(String addr) {
        this.addr = addr;
    }

    public static RpcClient getConnect(String addr) throws InterruptedException {
        if (clientMap.containsKey(addr)) {
            return clientMap.get(addr);
        }
        RpcClient con = connect(addr);
        clientMap.put(addr, con);
        return con;
    }

    private static RpcClient connect(String addr) throws InterruptedException {
        RpcClient client = new RpcClient(addr);

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4))
                        .addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())))
                        .addLast(new LengthFieldPrepender(4))
                        .addLast(new ObjectEncoder())
                        .addLast(new ClientHandler());
            }
        });

//        String port;
//        if(addr.contains(":")){
//
//        }
        ChannelFuture future = bootstrap.connect(addr, 8080).sync();
        LOGGER.info("client connect to " + addr + ":" + 8080);
        Channel c = future.channel();

        client.setChannel(c);
        client.setGroup(group);
        return client;
    }

    public RpcResponse invoke(RpcRequest request) throws Exception {
        ClientHandler handle = channel.pipeline().get(ClientHandler.class);
        Objects.nonNull(handle);
        return handle.invoke(request);
    }

    public void closeConnect() {
        this.group.shutdownGracefully();
    }


    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setGroup(EventLoopGroup group) {
        this.group = group;
    }

}
