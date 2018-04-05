package com.luangeng.starfish.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by LG on 2017/9/26.
 */
public class IPUtil {

    public static String getLoaclIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws UnknownHostException {
        System.out.println(IPUtil.getLoaclIP());
        System.out.println(InetAddress.getLocalHost().getAddress());
    }
}
