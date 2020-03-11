package com.dcg.socket.base;

/**
 * @ Time  :  2020-02-19
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description : socket 请求的接口
 */
public interface SocketClient {

    /**
     * socket 连接
     *
     * @param host ip地址
     * @param port 端口号
     * @return
     */
    boolean connect(String host, int port);

    /**
     * 发送数据
     *
     * @param action socket 发送的action
     * @param json   socket 发送的数据
     * @return
     * @throws Exception 异常信息
     */
    String sendMessage(String action, String json) throws Exception;
}
