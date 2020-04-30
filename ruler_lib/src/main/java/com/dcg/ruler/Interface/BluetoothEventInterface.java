package com.dcg.ruler.Interface;

import com.dcg.ruler.bean.BluetoothEventBean;

/**
 * author: Severn
 * date: 2020/4/26
 * email: shiszb@digitalchina.com
 * description:
 */
public interface BluetoothEventInterface {

    // 发布事件
    //void publishBluetoothEvent(String bluetoothEvent);
    // 订阅事件
    void subscribeBluetoothEvent(String bluetoothEvent , BluetoothEventBean bluetoothEventBean);

}
