package com.dcg.ruler.core;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import com.dcg.ruler.Interface.BluetoothEventInterface;
import com.dcg.ruler.bean.BluetoothEventBean;
import com.dcg.ruler.bean.LWHData;
import com.dcg.ruler.bean.PK20Data;
import com.dcg.ruler.service.BluetoothLeService;
import com.dcg.ruler.utils.Constants;
import com.dcg.ruler.utils.DataManageUtils;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.dcg.ruler.service.BluetoothLeService.ACTION_DATA_AVAILABLE;
import static com.dcg.ruler.service.BluetoothLeService.ACTION_GATT_CONNECTED;
import static com.dcg.ruler.service.BluetoothLeService.ACTION_GATT_DISCONNECTED;

/**
 * author: Severn
 * date: 2020/4/23
 * email: shiszb@digitalchina.com
 * description: 蓝牙广播相关功能
 */
public class BluetoothCore {

    public String name;
    public String address;
    private Context mContext;
    private int tryAgainConnect;
    public boolean wantDisconnect;
    public static final String TAG = "pk20";
    private static BluetoothCore mSingleInstance;
    public static BluetoothLeService mBluetoothLeService;
    public static BluetoothGattCharacteristic mNotifyCharacteristic3;
    public static BluetoothGattCharacteristic mNotifyCharacteristic6;

    public static BluetoothCore getInstance() {
        if (mSingleInstance == null) {
            synchronized (BluetoothCore.class) {
                if (mSingleInstance == null) {
                    mSingleInstance = new BluetoothCore();
                }
            }
        }
        return mSingleInstance;
    }

    public void init(Context context) {
        mContext = context;
    }

    /**
     * 绑定服务和注册广播接收者
     */
    public void bindServiceAndRegisterReceiver(BluetoothDevice device) {
        /*name = device.getName();
        address = device.getAddress();*/
        Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
        mContext.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        mContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private boolean isConnected;
    /**
     * 管理服务生命周期的代码，服务回调
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {// 无法初始化蓝牙
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.
            //在成功启动初始化时自动连接到设备。
            isConnected = mBluetoothLeService.connect(address);
            //Log.e(TAG, "onServiceConnected: connect = "+connect);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    /**
     * @return 返回是否已连接
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 想要断开蓝牙连接
     */
    public void wantDisconnectBle() {
        wantDisconnect = true;
    }

    // Handles various events fired by the Service.              处理由服务触发的各种事件。
    // ACTION_GATT_CONNECTED: connected to a GATT server.        连接到GATT服务器
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.与GATT服务器断开连接
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.发现了GATT服务
    // ACTION_DATA_AVAILABLE:                                    从设备接收数据，这可能是读取或通知操作的结果
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
                    mContext.unregisterReceiver(mGattUpdateReceiver);
                    mContext.unbindService(mServiceConnection);
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

    public void writeCharacteristic3(String s) {
        if (mNotifyCharacteristic3 != null) {
            byte[] data = DataManageUtils.HexString2Bytes(s);//转十六进制
            mNotifyCharacteristic3.setValue(data);
            mBluetoothLeService.wirteCharacteristic(mNotifyCharacteristic3);
        }
    }

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

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        SystemClock.sleep(150);
        mBluetoothLeService.setCharacteristicNotification(characteristic, enabled);
    }

    public void connect() {
        mBluetoothLeService.connect(address);
    }

    public void disconnect() {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.disconnect();
        }
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

    public void getDeviceName(BluetoothDevice device) {
        name = device.getName();
        address = device.getAddress();
        bindServiceAndRegisterReceiver(device);
        mContext.registerReceiver(mGattUpdateReceiver2, makeGattUpdateIntentFilter2());
    }

    private IntentFilter makeGattUpdateIntentFilter2() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }

    // Handles various events fired by the Service.处理由服务触发的各种事件。
    // ACTION_GATT_CONNECTED: connected to a GATT server.连接到GATT服务器
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.与GATT服务器断开连接
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.发现了GATT服务
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    //从设备接收数据。这可能是阅读的结果或通知操作。
    private final BroadcastReceiver mGattUpdateReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_CONNECTED.equals(action)) {
                //EventBus.getDefault().post(new MsgEvent("KP", false));
                if (bluetoothEvent != null) {
                    bluetoothEvent.subscribeBluetoothEvent(Constants.KP_FALSE, null);
                }

                boolean cn = mContext.getResources().getConfiguration().locale.getCountry().equals("CN");

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    /**
                     *要执行的操作
                     */
                    if (cn) {
                        Toast.makeText(mContext, "已连接", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "Connection", Toast.LENGTH_LONG).show();
                    }
                }, 1000); //1秒后执行Runnable中的run方法,否则初始化失败

                //EventBus.getDefault().post(new MsgEvent("ServiceConnectedStatus", true));
                if (bluetoothEvent != null) {
                    bluetoothEvent.subscribeBluetoothEvent(Constants.SERVICECONNECTEDSTATUS_TRUE, null);
                }

            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {

                //EventBus.getDefault().post(new MsgEvent("ServiceConnectedStatus", false));
                if (bluetoothEvent != null) {
                    bluetoothEvent.subscribeBluetoothEvent(Constants.SERVICECONNECTEDSTATUS_FALSE, null);
                }

                boolean cn = mContext.getResources().getConfiguration().locale.getCountry().equals("CN");
                if (cn) {
                    Toast.makeText(mContext, "已断开", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "Disconnect", Toast.LENGTH_LONG).show();
                }

                Log.d("ZM_connect", "application里面的断开连接");
                if (wantDisconnect) {
                    mContext.unregisterReceiver(mGattUpdateReceiver2);
                } else {
                    //EventBus.getDefault().post(new MsgEvent("KP", true));
                    if (bluetoothEvent != null) {
                        bluetoothEvent.subscribeBluetoothEvent(Constants.KP_TRUE, null);
                    }
                }
            } else if (ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if (TextUtils.isEmpty(data)) {
                    LWHData lwh = intent.getParcelableExtra(BluetoothLeService.NOTIFICATION_DATA_LWH);
                    if (lwh != null) {
                        //EventBus.getDefault().post(new MsgEvent("LWHData", lwh));
                        if (bluetoothEvent != null) {
                            BluetoothEventBean bluetoothEventBean = new BluetoothEventBean();
                            bluetoothEventBean.setWidth(lwh.W);
                            bluetoothEventBean.setLength(lwh.L);
                            bluetoothEventBean.setHeight(lwh.H);
                            bluetoothEvent.subscribeBluetoothEvent(Constants.LWHDATA_LWH, bluetoothEventBean);
                        }
                    } else {
                        String dataERR = intent.getStringExtra(BluetoothLeService.NOTIFICATION_DATA_ERR);
                        BluetoothEventBean bluetoothEventBean = new BluetoothEventBean();
                        if (TextUtils.isEmpty(dataERR)) {
                            PK20Data mPK20Data = intent.getParcelableExtra(BluetoothLeService.NOTIFICATION_DATA);
                            if (bluetoothEvent != null) {
                                bluetoothEventBean = new BluetoothEventBean();
                                bluetoothEventBean.setWidth(mPK20Data.W);
                                bluetoothEventBean.setLength(mPK20Data.L);
                                bluetoothEventBean.setHeight(mPK20Data.H);
                                bluetoothEvent.subscribeBluetoothEvent(Constants.LWHDATA_LWH, bluetoothEventBean);
                            }
                            /*if (dataStorageSuccessListener != null) {
                                dataStorageSuccessListener.onDataStorageSuccess(mPK20Data);
                            }*/
                            boolean cn = mContext.getResources().getConfiguration().locale.getCountry().equals("CN");
                            if (bluetoothEvent != null) {
                                if (cn) {
                                    bluetoothEventBean.setTips("数据存储成功");
                                    bluetoothEvent.subscribeBluetoothEvent(Constants.SAVE6DATA_SUCCESS, bluetoothEventBean);
                                } else {
                                    bluetoothEventBean.setTips("Data storage success");
                                    bluetoothEvent.subscribeBluetoothEvent(Constants.SAVE6DATA_SUCCESS, bluetoothEventBean);
                                }
                            }
                        } else {
                            if (bluetoothEvent != null) {
                                bluetoothEventBean.setTips(dataERR);
                                bluetoothEvent.subscribeBluetoothEvent(Constants.SAVE6DATA_DATAERR, bluetoothEventBean);
                            }
                        }
                    }

                }
            }
        }
    };

    // 获取蓝牙数据，回调
    private getBluetoothLeDataListener listener;
    public interface getBluetoothLeDataListener {
        void getData(String data);
    }
    public void setGetBluetoothLeDataListener(getBluetoothLeDataListener listener) {
        this.listener = listener;
    }

    // 数据存储成功，回调
    public interface  OnDataStorageSuccessListener{
        void onDataStorageSuccess(PK20Data pk20Data);
    }
    private OnDataStorageSuccessListener dataStorageSuccessListener;
    public void setOnDataStorageSuccessListener(OnDataStorageSuccessListener listener){
        dataStorageSuccessListener = listener;
    }

    // 自定义的广播事件分发
    private BluetoothEventInterface bluetoothEvent;
    public void setOnBluetoothEventListener(BluetoothEventInterface bluetoothEvent) {
        this.bluetoothEvent = bluetoothEvent;
    }

    // 自定义的其他事件分发
    /*public void publishBluetoothEvent(String bluetoothEvent , BluetoothEventBean bluetoothEventBean) {
        if (this.bluetoothEvent == null) {
            this.bluetoothEvent = this;
        }
        subscribeBluetoothEvent(bluetoothEvent, bluetoothEventBean);
    }

    @Override
    public void subscribeBluetoothEvent(String bluetoothEvent , BluetoothEventBean bluetoothEventBean) {
        this.bluetoothEvent.subscribeBluetoothEvent(bluetoothEvent, bluetoothEventBean);
    }*/

}
