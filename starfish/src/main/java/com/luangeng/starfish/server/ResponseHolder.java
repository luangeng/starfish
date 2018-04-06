package com.luangeng.starfish.server;

import com.luangeng.starfish.common.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LG on 2017/11/29.
 */
public class ResponseHolder {

    //request Id 与 response的映射
    private static Map<Long, ResponseHolder> responseMap = new ConcurrentHashMap<Long, ResponseHolder>();

    private RpcResponse response;

    public static ResponseHolder newHolder(Long id) {
        ResponseHolder holder = new ResponseHolder();
        responseMap.put(id, holder);
        return holder;
    }

    public RpcResponse getResponse() throws Exception {
        synchronized (this) {
            wait(10000);
        }
        responseMap.remove(response.getId());
        return response;
    }

    public static void setResponse(RpcResponse response) {
        ResponseHolder holder = responseMap.get(response.getId());
        if (holder != null) {
            holder.response = response;
            synchronized (holder) {
                holder.notify();
            }
        }
    }

}
