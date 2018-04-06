package com.luangeng.starfish.server;

import com.luangeng.starfish.common.HelloService;
import com.luangeng.starfish.common.Pojo;

public class HelloServerImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }

    @Override
    public String hello(String msg, String name) {
        return msg + name;
    }

    @Override
    public Pojo test(Pojo pojo) {
        pojo.setId(pojo.getId() + 1000);
        pojo.setName(pojo.getName().toUpperCase());
        pojo.setMan(!pojo.isMan());
        ////pojo.getList().add("last");
        return pojo;
    }

}
