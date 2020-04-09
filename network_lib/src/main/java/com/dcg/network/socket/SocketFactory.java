package com.dcg.network.socket;

import com.dcg.network.socket.netty.NettyClient;

/**
 * @ Time  :  2020-02-19
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description : Socket 请求的工厂类，用来得到一个Socket client
 */
public class SocketFactory {

    public static SocketClient getSocketClient() {
        return NettyClient.getInstance();
    }
}
