package com.dcg.ruler.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import com.dcg.ruler.service.BluetoothLeService;
import com.dcg.ruler.utils.DataManageUtils;

import static com.dcg.ruler.service.BluetoothLeService.ACTION_DATA_AVAILABLE;
import static com.dcg.ruler.service.BluetoothLeService.ACTION_GATT_CONNECTED;
import static com.dcg.ruler.service.BluetoothLeService.ACTION_GATT_DISCONNECTED;

/**
 * Created by 张明_ on 2017/9/5.
 */

public class BaseBleApplication extends Application {
    public static final String TAG = "pk20";
    public static BluetoothLeService mBluetoothLeService = null;
    public static BluetoothGattCharacteristic mNotifyCharacteristic3 = null;
    public static BluetoothGattCharacteristic mNotifyCharacteristic6 = null;
    private String address = "";
    private String name = "";
    private int tryAgainConnect = 0;
    public boolean wantDisconnect = false;

    public void bindServiceAndRegisterReceiver(BluetoothDevice device) {
        address = device.getAddress();
        name = device.getName();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        this.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.
            //在成功启动初始化时自动连接到设备。
            boolean connect = mBluetoothLeService.connect(address);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    public void wantDisconnectBle() {
        wantDisconnect = true;
    }

    // Handles various events fired by the Service.处理由服务触发的各种事件。
    // ACTION_GATT_CONNECTED: connected to a GATT server.连接到GATT服务器
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.与GATT服务器断开连接
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.发现了GATT服务
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    //从设备接收数据。这可能是阅读的结果或通知操作。
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_CONNECTED.equals(action)) {
                tryAgainConnect = 0;
            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
                tryAgainConnect++;
                if (tryAgainConnect > 3 || wantDisconnect) {
                    mBluetoothLeService.close();
                    mBluetoothLeService = null;
                    mNotifyCharacteristic3 = null;
                    mNotifyCharacteristic6 = null;
                    address = null;
                    name = null;
                    unregisterReceiver(mGattUpdateReceiver);
                    unbindService(mServiceConnection);
                } else {
                    connect();
                }


            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //显示用户界面上所有受支持的服务和特性。
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if (!TextUtils.isEmpty(data)) {
                    sendBluetoothLeData(data);
                }
            }
        }
    };


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

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        SystemClock.sleep(150);
        mBluetoothLeService.setCharacteristicNotification(characteristic, enabled);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mBluetoothLeService = null;
    }

    public void connect() {
        mBluetoothLeService.connect(address);
    }

    public void disconnect() {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.disconnect();
        }
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

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @SuppressLint("NewApi")
    public void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) {
            return;
        }
        String uuid = null;
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            Log.d(TAG, "displayGattServices: " + uuid);
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();
                if (uuid.equals("0000fff3-0000-1000-8000-00805f9b34fb")) {
                    mNotifyCharacteristic3 = gattCharacteristic;
                    setCharacteristicNotification(mNotifyCharacteristic3, true);
                } else if (uuid.equals("0000fff6-0000-1000-8000-00805f9b34fb")) {
                    mNotifyCharacteristic6 = gattCharacteristic;
                    setCharacteristicNotification(mNotifyCharacteristic6, true);
                }
            }
        }
    }
}
