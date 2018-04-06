package com.luangeng.starfish.common;

import com.luangeng.starfish.server.RpcServer;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by LG on 2017/9/28.
 */
public class ServiceCenter implements Watcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCenter.class);

    private static final String APPS_PATH = "/__apps__";
    private static ZooKeeper zk;

    private CountDownLatch latch = new CountDownLatch(1);

    //服务中心地址
    private String addr;

    //服务的名称和版本
    private String name;
    private String version;

    private ServiceCenter(String addr, String name) {
        this(addr, name, "");
    }

    private ServiceCenter(String addr, String name, String version) {
        this.addr = addr;
        this.name = name;
        this.version = version;

        connect();
        String ip = IPUtil.getLoaclIP();
        register(ip);
        RpcServer.startUp();
    }

    private void connect() {
        if (zk != null) {
            return;
        }
        try {
            zk = new ZooKeeper(addr, 30000, this);
            latch.await();
            if (zk.exists(APPS_PATH, false) == null) {
                zk.create(APPS_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean register(String address) {
        try {
            connect();
            if (zk.exists(APPS_PATH + "/" + name, false) == null) {
                zk.create(APPS_PATH + "/" + name, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            String path = zk.create(APPS_PATH + "/" + name + "/" + address, address.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            LOGGER.info("register success -> " + path);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<String> queryService(String serviceName) throws KeeperException, InterruptedException {
        List<String> apps = zk.getChildren(APPS_PATH + "/" + serviceName, false);
        if (apps == null) {
            return Collections.emptyList();
        }
        //Collections.sort(apps);
        //byte[] data = zk.getData(apps.get(0), false, null);
        return apps;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            latch.countDown();
        }
    }
}
