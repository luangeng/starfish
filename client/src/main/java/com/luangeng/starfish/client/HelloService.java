package com.luangeng.starfish.client;

public interface HelloService {

    String hello(String name);

    String hello(String msg, String name);

    Pojo test(Pojo pojo);
}