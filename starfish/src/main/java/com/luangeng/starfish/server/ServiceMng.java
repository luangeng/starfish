package com.luangeng.starfish.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LG on 2017/11/28.
 */
public class ServiceMng {

    //所有提供的服务
    private static Map<String, Object> serviceMap = new HashMap<>();

    ServiceMng(String pkg) {

    }

    public static Object getService(String name) {
        return serviceMap.get(name);
    }

    public void setApplicationContext(String a) {
        Map<String, Object> beansMap = null;//context.getBeansWithAnnotation(RpcClass.class);
        for (Map.Entry entry : beansMap.entrySet()) {
            String interfaceName = entry.getValue().getClass().getAnnotation(RpcClass.class).value().getName();
            serviceMap.put(interfaceName, entry.getValue());
        }
        if (!serviceMap.isEmpty()) {
            //ServiceCenter.register("DemoService", IPUtil.getLoaclIP()+"8080");
            RpcServer.startUp();
        }
    }

}