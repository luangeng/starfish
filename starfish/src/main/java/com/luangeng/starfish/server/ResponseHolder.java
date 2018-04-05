package com.luangeng.starfish.server;

import com.luangeng.starfish.common.RpcResponse;

/**
 * Created by LG on 2017/11/29.
 */
public class ResponseHolder {
    private RpcResponse response;

    public RpcResponse getResponse() throws Exception {
        synchronized (this) {
            wait(10000);
        }
        return response;
    }

    public void setResponse(RpcResponse response) {
        this.response = response;
        synchronized (this) {
            notify();
        }
    }

    public RpcResponse getResponse(long timeout) throws Exception {
        synchronized (this) {
            wait(timeout);
        }
        return response;
    }
}
