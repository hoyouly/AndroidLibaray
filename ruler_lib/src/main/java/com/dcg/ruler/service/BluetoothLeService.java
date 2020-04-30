package com.dcg.ruler.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.dcg.ruler.bean.LWHData;
import com.dcg.ruler.bean.PK20Data;
import com.dcg.ruler.bean.SampleGattAttributes;
import com.dcg.ruler.utils.ByteUtils;
import com.dcg.ruler.utils.DataManageUtils;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
@SuppressLint("NewApi")
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    public final static String NOTIFICATION_DATA = "com.example.bluetooth.le.NOTIFICATION_DATA";
    public final static String NOTIFICATION_DATA_LWH = "com.example.bluetooth.le.NOTIFICATION_DATA_LWH";
    public final static String NOTIFICATION_DATA_ERR = "com.example.bluetooth.le.NOTIFICATION_DATA_ERR";

    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                mBluetoothGatt.close();
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        //discoverServices 搜索连接设备所支持的service。
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        //readCharacteristic 读取指定的characteristic。
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        //setCharacteristicNotification 设置当指定characteristic值变化时，发出通知。
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        //写入回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Handler handler = new Handler(Looper.getMainLooper());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                setCharacteristicNotification(characteristic, true);
            } else if (status == BluetoothGatt.GATT_FAILURE) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        boolean cn = BluetoothLeService.this.getResources().getConfiguration().locale.getCountry().equals("CN");
                        if (cn) {
                            Toast.makeText(BluetoothLeService.this, "写入失败", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(BluetoothLeService.this, "Write failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else if (status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        boolean cn = BluetoothLeService.this.getResources().getConfiguration().locale.getCountry().equals("CN");
                        if (cn) {
                            Toast.makeText(BluetoothLeService.this, "没有权限", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(BluetoothLeService.this, "No permission", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    };
    private List<byte[]> mByteList = new ArrayList<>();

    public void wirteCharacteristic(BluetoothGattCharacteristic characteristic) {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
            sendBroadcast(intent);
        } else if ("0000fff6-0000-1000-8000-00805f9b34fb".equals(characteristic.getUuid().toString())) {
            final byte[] data = characteristic.getValue();
            Log.d("ZM", "接收数据: " + ByteUtils.toHexString(data));
            if (data[3] == (byte) 0xB1 && data[0] == (byte) 0xAA && data[1] == (byte) 0x0A) {
                mByteList.clear();
            }
            mByteList.add(data);
            if (mByteList.size() == 9) {
                final List<byte[]> mByteNewList = new ArrayList<>();
                mByteNewList.addAll(mByteList);
                mByteList.clear();
                byte[] bytes0 = mByteNewList.get(0);
                for (int i = 0; i < mByteNewList.size(); i++) {
                    String bytesToHexString = DataManageUtils.bytesToHexString(mByteNewList.get(i));
                    Log.d("PK20", "broadcastUpdate: " + bytesToHexString);
                }
                int jiaoYan6 = DataManageUtils.jiaoYan6(mByteNewList.get(0), mByteNewList.get(8));
                if (jiaoYan6 != 0) {
                    boolean cn = BluetoothLeService.this.getResources().getConfiguration().locale.getCountry().equals("CN");
                    if (cn) {
                        intent.putExtra(NOTIFICATION_DATA_ERR, "信道6数据有误");
                    } else {
                        intent.putExtra(NOTIFICATION_DATA_ERR, "ERROR");
                    }
                    sendBroadcast(intent);
                    return;
                }
                StringBuilder stringBuffer = new StringBuilder();
                if (bytes0[3] != (byte) 0xB1) {
                    stringBuffer.append("B1");
                }
                if (mByteNewList.get(1)[0] != (byte) 0xB2)
                if (mByteNewList.get(2)[0] != (byte) 0xB3) {{
                    stringBuffer.append("B2");
                }
                    stringBuffer.append("B3");
                }
                if (mByteNewList.get(3)[0] != (byte) 0xB4) {
                    stringBuffer.append("B4");
                }
                if (mByteNewList.get(4)[0] != (byte) 0xB5) {
                    stringBuffer.append("B5");
                }
                if (mByteNewList.get(5)[0] != (byte) 0xB6) {
                    stringBuffer.append("B6");
                }
                if (mByteNewList.get(6)[0] != (byte) 0xB7) {
                    stringBuffer.append("B7");
                }
                if (mByteNewList.get(7)[0] != (byte) 0xB8) {
                    stringBuffer.append("B8");
                }
                if (mByteNewList.get(8)[0] != (byte) 0xB9) {
                    stringBuffer.append("B9");
                }
                if (TextUtils.isEmpty(stringBuffer.toString())) {
                    Log.d("ZM", "数据接收到进行解析保存: " + System.currentTimeMillis());
                    mThread m = new mThread(mByteNewList, characteristic, intent);
                    m.start();
                } else {
                    String toString = stringBuffer.toString();
                    int length = toString().length() / 2 + 1;
                    StringBuilder zero = new StringBuilder();
                    for (int i = 0; i < 15 - length + 1; i++) {
                        zero.append("00");
                    }
                    String jiaoYan = DataManageUtils.getJiaoYan(toString.substring(0, 2)
                            , toString.substring(toString.length() - 2, toString.length()));
                    zero.append(jiaoYan);
//                    String result = "AA02" + DataManageUtils.toHexString(length) + toString + zero + "00";
//                    Log.d("ZM", "信道6数据部分重发: " + result);
//                    BaseBleApplication.wri
                    boolean cn = BluetoothLeService.this.getResources().getConfiguration().locale.getCountry().equals("CN");
                    if (cn) {
                        intent.putExtra(NOTIFICATION_DATA_ERR, "信道6数据部分重发");
                    } else {
                        intent.putExtra(NOTIFICATION_DATA_ERR, "ERROR");
                    }
                    sendBroadcast(intent);
                }
            } else {
                Log.d("ZM", "信道6的条码x: ");
            }

        } else {
            Log.d("ZM", "信道6的条码1: " );
            // For all other profiles, writes the data formatted in HEX.
            // 对于所有其他配置文件，用十六进制格式编写数据。
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                Log.d("ZM", "信道6的条码2: " );
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
                if (data[1] == (byte) 0x14) {
                    Log.d("ZM", "信道6的条码3: " );
                    int ff = DataManageUtils.jiaoYanLWHData(stringBuilder.toString(), "FF", "14");
                    if (ff == 0) {
                        sendLWHData(intent, data);
                    }
                } else {
                    Log.d("ZM", "信道6的条码: " + stringBuilder.toString());
                    intent.putExtra(EXTRA_DATA, stringBuilder.toString());
                    sendBroadcast(intent);
                }
            }
        }
    }

    //发送信道3长宽高信息
    private void sendLWHData(Intent intent, byte[] data) {
        String itemW = "";
        String itemH = "";
        String itemL = "";
        String length = DataManageUtils.getLWH(data, DataManageUtils.L3);
        int l = 0;
        if (!length.equals("ffff")) {
            l = Integer.parseInt(length, 16);
            double result = (double) l / 10;
            itemL = result + "";
        }

        String wStr = DataManageUtils.getLWH(data, DataManageUtils.W3);
        int w = 0;
        if (!wStr.equals("ffff")) {
            w = Integer.parseInt(wStr, 16);
            double result = (double) w / 10;
            itemW = result + "";
        }

        String hStr = DataManageUtils.getLWH(data, DataManageUtils.H3);
        int h = 0;
        if (!hStr.equals("ffff")) {
            h = Integer.parseInt(hStr, 16);
            double result = (double) h / 10;
            itemH = result + "";
        }

        LWHData lwhData = new LWHData(itemL, itemW, itemH);
        intent.putExtra(NOTIFICATION_DATA_LWH, lwhData);
        sendBroadcast(intent);
    }


    class mThread extends Thread {
        List<byte[]> mByteNewList;
        BluetoothGattCharacteristic characteristic;
        Intent intent;

        public mThread(List<byte[]> mByteNewList, BluetoothGattCharacteristic characteristic, Intent intent) {
            this.mByteNewList = mByteNewList;
            this.characteristic = characteristic;
            this.intent = intent;
        }

        @Override
        public void run() {
            String expressCode = DataManageUtils.getExpressCode(mByteNewList.get(3), mByteNewList.get(4));
            String barCode = "";
            if (!"ffffffffffffffffffffffffffffffffffffffff".equals(expressCode)) {
                barCode = DataManageUtils.convertHexToString(expressCode).replace("\u0000", "");
            }

            String branchCode = DataManageUtils.getBranchCode(mByteNewList.get(0));
            String wangDian = "";
            if (!branchCode.equals("ffffffffffffffffffff")) {
                wangDian = DataManageUtils.convertHexToString(branchCode).replace("\u0000", "");
            }

            String centerCode = DataManageUtils.getCenterCode(mByteNewList.get(0), mByteNewList.get(1));
            String center = "";
            if (!centerCode.equals("ffffffffffffffffffff")) {
                center = DataManageUtils.convertHexToString(centerCode).replace("\u0000", "");
            }

            String muDiCode = DataManageUtils.getMuDiCode(mByteNewList.get(1));
            String muDi = "";
            if (!muDiCode.equals("ffffffffffffffffffff")) {
                muDi = DataManageUtils.convertHexToString(muDiCode).replace("\u0000", "");
            }

            String use = DataManageUtils.getUse(mByteNewList.get(1));
            String liuCheng = DataManageUtils.convertHexToString(use);

            String mac = DataManageUtils.getMac(mByteNewList.get(6), mByteNewList.get(7));
            String identify = DataManageUtils.getIdentify(mByteNewList.get(7));
            String itemL = "";
            String length = DataManageUtils.getLWHG(mByteNewList.get(2), DataManageUtils.L);
            int l = 0;
            if (!length.equals("ffff")) {
                l = Integer.parseInt(length, 16);
                double result = (double) l / 10;
                itemL = result + "";
            }

            String itemW = "";
            String itemH = "";
            String itemV = "";

            if ("00".equals(identify)) {
                //长宽高数据都有效(体积）
                String wStr = DataManageUtils.getLWHG(mByteNewList.get(2), DataManageUtils.W);
                int w = 0;
                if (!wStr.equals("ffff")) {
                    w = Integer.parseInt(wStr, 16);
                    double result = (double) w / 10;
                    itemW = result + "";
                }

                String hStr = DataManageUtils.getLWHG(mByteNewList.get(2), DataManageUtils.H);
                int h = 0;
                if (!hStr.equals("ffff")) {
                    h = Integer.parseInt(hStr, 16);
                    double result = (double) h / 10;
                    itemH = result + "";
                }

                String vStr = DataManageUtils.getV(mByteNewList.get(2));
                long v = 0;
                if (!vStr.equals("ffffffff")) {
                    v = Long.parseLong(vStr, 16);
                    double tijizhong = (double) v / 100;
                    itemV = tijizhong + "";
                }
            }

            String gStr = DataManageUtils.getLWHG(mByteNewList.get(2), DataManageUtils.G);
            String itemG = "";
            int g = 0;
            if (!gStr.equals("ffff")) {
                g = Integer.parseInt(gStr, 16);
                itemG = g + "";
            }

            String time = DataManageUtils.getTime(mByteNewList.get(2));
            String timeResult = "";
            if (!time.equals("ffffffffffff")) {
                timeResult = "20" + time.substring(10, 12) + "-"
                        + time.substring(8, 10) + "-"
                        + time.substring(6, 8) + " "
                        + time.substring(4, 6) + ":"
                        + time.substring(2, 4) + ":"
                        + time.substring(0, 2);
            }

            String mainCode = DataManageUtils.getMainCode(mByteNewList.get(4), mByteNewList.get(5));
            String zhu = "";
            if (!mainCode.equals("ffffffffffffffffffffffffffffffffffffffff")) {
                zhu = DataManageUtils.convertHexToString(mainCode).replace("\u0000", "");
            }

            String sonCode = DataManageUtils.getSonCode(mByteNewList.get(5), mByteNewList.get(6));
            String zi = "";
            if (!sonCode.equals("ffffffffffffffffffffffffffffffffffffffff")) {
                zi = DataManageUtils.convertHexToString(sonCode).replace("\u0000", "");
            }

            String flag = DataManageUtils.getFlag(mByteNewList.get(8));
            String biaoji = "";
            if (!"ff".equals(flag)) {
                biaoji = DataManageUtils.convertHexToString(flag);
            }

            String name = DataManageUtils.getName(mByteNewList.get(7), mByteNewList.get(8));
            PK20Data mData = new PK20Data(barCode, wangDian, center, muDi, liuCheng, itemL, itemW, itemH, itemV
                    , itemG, timeResult, zhu, zi, mac, identify, biaoji, name);

            intent.putExtra(NOTIFICATION_DATA, mData);
            sendBroadcast(intent);
        }
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     * 连接到在蓝牙LE设备上托管的GATT服务器。
     *
     * @param address The device address of the destination device.目标设备的设备地址。
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.以前连接设备。尝试重新连接。
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.我们想要直接连接到设备上，所以我们设置了自动连接 参数为false。
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.在使用了一个可使用的设备之后，应用程序必须调用这个方法来确保资源的使用。
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     * 启用或禁用给定特性的通知。
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        boolean isEnableNotification = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        if (isEnableNotification) {
            List<BluetoothGattDescriptor> descriptorList = characteristic.getDescriptors();
            if (descriptorList != null && descriptorList.size() > 0) {
                for (BluetoothGattDescriptor descriptor : descriptorList) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                }
            }
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     * 检索连接设备上支持的GATT服务的列表
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
}
