package com.luangeng.starfish.client;

import com.luangeng.starfish.common.RpcRequest;
import com.luangeng.starfish.common.RpcResponse;
import com.luangeng.starfish.common.ServiceCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by LG on 2017/9/28.
 */
public class RpcProxy implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);

    private static AtomicLong id = new AtomicLong(0);

    private String name;

    RpcProxy(String name) {
        this.name = name;
    }

    public static <T> T get(String name, Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcProxy(name)
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

        List<String> addr = ServiceCenter.queryService(name);
        RpcClient client = RpcClient.getConnect(addr.get(0));
        RpcResponse r = client.invoke(request);
        return r.getResult();
    }

}
