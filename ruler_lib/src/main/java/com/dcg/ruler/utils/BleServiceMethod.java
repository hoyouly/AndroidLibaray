package com.dcg.ruler.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;

import com.dcg.ruler.service.BluetoothLeService;

/**
 *
 * @author 张明_
 * @date 2017/9/5
 */

public class BleServiceMethod {
    private BluetoothLeService mBluetoothLeService = null;
    private BluetoothGattCharacteristic mNotifyCharacteristic3 = null;
    private BluetoothGattCharacteristic mNotifyCharacteristic6 = null;
    private BluetoothDevice device = null;

    public BleServiceMethod(BluetoothLeService mBluetoothLeService, BluetoothGattCharacteristic mNotifyCharacteristic3,
                            BluetoothGattCharacteristic mNotifyCharacteristic6, BluetoothDevice device) {
        this.mBluetoothLeService = mBluetoothLeService;
        this.mNotifyCharacteristic3 = mNotifyCharacteristic3;
        this.mNotifyCharacteristic6 = mNotifyCharacteristic6;
        this.device = device;
    }

    @SuppressLint("NewApi")
    public void writeCharacteristic3(String s) {
        if (mNotifyCharacteristic3 != null) {
            byte[] data = DataManageUtils.HexString2Bytes(s);//转十六进制
            mNotifyCharacteristic3.setValue(data);
            mBluetoothLeService.wirteCharacteristic(mNotifyCharacteristic3);
        }
    }

    @SuppressLint("NewApi")
    public void writeCharacteristic6(String s) {
        if (mNotifyCharacteristic6 != null) {
            byte[] data = DataManageUtils.HexString2Bytes(s);//转十六进制
            mNotifyCharacteristic6.setValue(data);
            mBluetoothLeService.wirteCharacteristic(mNotifyCharacteristic6);
        }
    }

    public void readCharacteristic6() {
        if (mNotifyCharacteristic6 != null) {
            mBluetoothLeService.readCharacteristic(mNotifyCharacteristic6);
        }
    }

    public void readCharacteristic3() {
        if (mNotifyCharacteristic3 != null) {
            mBluetoothLeService.readCharacteristic(mNotifyCharacteristic3);
        }
    }

    public void setCharacteristicNotification6(BluetoothGattCharacteristic characteristic,
                                               boolean enabled) {
        mBluetoothLeService.setCharacteristicNotification(characteristic, enabled);
    }


    public void connect() {
        mBluetoothLeService.connect(device.getAddress());
    }

    public void disconnect() {
        mBluetoothLeService.disconnect();
    }

    public interface getBluetoothLeDataListener {
        void getData(String data);
    }

    private getBluetoothLeDataListener listener;

    public void setGetBluetoothLeDataListener(getBluetoothLeDataListener listener) {
        this.listener = listener;
    }

    private void sendBluetoothLeData(String data) {
        if (listener != null) {
            listener.getData(data);
        }
    }
}
