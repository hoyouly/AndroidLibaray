package com.dcg.network.socket;

/**
 * @ Time  :  2020-02-19
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description : 连接状态的监听
 */
public interface OnConnectListener {

    /**
     * 连接状态
     *
     * @param connect 是否连接成功
     */
    void connectState(boolean connect);

}
