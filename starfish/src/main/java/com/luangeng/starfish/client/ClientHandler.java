package com.luangeng.starfish.client;

import com.luangeng.starfish.common.RpcRequest;
import com.luangeng.starfish.common.RpcResponse;
import com.luangeng.starfish.server.ResponseHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LG on 2017/11/26.
 */
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    private Channel channel;

    //request Id 与 response的映射
    private Map<Long, ResponseHolder> responseMap = new ConcurrentHashMap<Long, ResponseHolder>();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        ResponseHolder holder = responseMap.get(response.getId());
        if (holder != null) {
            responseMap.remove(response.getId());
            holder.setResponse(response);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        channel = ctx.channel();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("exceptionCaught", cause);
        ctx.close();
    }

    public RpcResponse invoke(RpcRequest request) throws Exception {
        ResponseHolder holder = new ResponseHolder();
        responseMap.put(request.getId(), holder);
        channel.writeAndFlush(request);
        return holder.getResponse();
    }

}
