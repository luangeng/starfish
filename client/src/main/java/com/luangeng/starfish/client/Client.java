package com.luangeng.starfish.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collections;
import java.util.Date;

/**
 * Created by LG on 2017/11/29.
 */
public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws InterruptedException {

        //
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        System.out.println("Client start");

        //
        HelloService hello = RpcProxy.get("server1", HelloService.class);

        for (int i = 0; i < 10; i++) {

            String result = hello.hello("luangeng" + i);
            LOGGER.info("result: " + result);

            result = hello.hello("你好，", "luangeng" + i);
            LOGGER.info("result: " + result);

            Pojo pojo = new Pojo();
            pojo.setId(i);
            pojo.setName("luanegng" + i);
            pojo.setMan(true);
            pojo.setBirth(new Date());
            pojo.setList(Collections.singletonList("pojo" + i));

            Pojo p2 = hello.test(pojo);
            LOGGER.info(p2.toString());
            LOGGER.info("");
        }

    }
}
