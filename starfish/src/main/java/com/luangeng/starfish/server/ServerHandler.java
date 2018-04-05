package com.luangeng.starfish.server;

import com.luangeng.starfish.common.RpcRequest;
import com.luangeng.starfish.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by LG on 2017/9/28.
 */
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        RpcResponse response = new RpcResponse();
        //LOGGER.info("server read:" + request.toString());
        response.setId(request.getId());
        try {
            Object result = invoke(request);
            response.setResult(result);
        } catch (Exception e) {
            response.setException(e);
        }
        ctx.writeAndFlush(response);
        //LOGGER.info("server send:" + response.toString());
    }

    public Object invoke(RpcRequest request) throws Exception {
        String classname = request.getClassName();
        String methodname = request.getMethodName();
        Class[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        Object o = ServiceMng.getService(classname);
        Class clazz = o.getClass();
        Method method = clazz.getMethod(methodname, parameterTypes);
        Object result = method.invoke(o, parameters);
        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }
}
