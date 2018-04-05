package com.luangeng.starfish.client;

import com.luangeng.starfish.common.RpcRequest;
import com.luangeng.starfish.common.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by LG on 2017/9/28.
 */
public class RpcProxy implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);

    private static AtomicLong id = new AtomicLong(0);

    private RpcClient client = null;

    public static <T> T get(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcProxy()
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setId(id.incrementAndGet());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        //

        if (client == null) {
            client = RpcClient.getConnect("localhost", 8080);
        }
        RpcResponse r = client.invoke(request);
        return r.getResult();
    }

    public void close() {
        this.client.closeConnect();
    }
}
