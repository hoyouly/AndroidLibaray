package com.dcg.rfid;

import android.util.Log;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;

/**
 * RFID单扫工具类
 */
public class RFIDScanManager {

    // 普通扫描广播
    public static final String ACTION_SEND_EPC = "com.dcg.data.epc";
    //批量扫描 停止RFID 扫描
    public static final String STOP_SCAN = "com.dcg.action.stop_uhf";
    //批量扫描 中的开始扫描广播
    public static final String START_SCAN = "com.dcg.action.start_uhf";

    // 按键扫描广播  单个RFID扫描结果
    public static final String ACTION_SEND_BASE_EPC = "com.se4500.onDecodeComplete";

    private Context mContext;
    private IntentFilter intentFilter;
    private boolean inputFromBroadcast;
    private static RFIDScanManager rfidScanManager = null;
    private Intent startScannIntent, stopScannIntent;

    //是否注册广播
    private boolean isSingleRegister;
    private boolean isBatchRegister;

    private RFIDScanManager(Context context) {
        this.mContext = context;
    }

    public static RFIDScanManager getInstance(Context context) {
        if (rfidScanManager == null) {
            synchronized (RFIDScanManager.class) {
                if (rfidScanManager == null) {
                    rfidScanManager = new RFIDScanManager(context);
                }
            }
        }
        return rfidScanManager;
    }

    /**
     * 注册单次RFID扫描广播
     *
     */
    public void registerSingleRfid() {
        Log.d("RFIDScanManager", "RFIDScanManager : registerSingleRfid " + mContext);
        intentFilter = new IntentFilter();
        // 单次扫描
        intentFilter.addAction(ACTION_SEND_BASE_EPC);
        mContext.registerReceiver(singleRifdReciver, intentFilter);
        isSingleRegister = true;
    }


    /**
     * 单次RFID扫描取消注册
     */
    public void unregitserSingleRfid() {
        Log.d("RFIDScanManager", "RFIDScanManager : unregitserSingleRfid  isSingleRegister: " + isSingleRegister + "   mContext  " + mContext);
        if (isSingleRegister) {
            mContext.unregisterReceiver(singleRifdReciver);
            isSingleRegister = false;
        }
    }

    /**
     * 开启批量RFID扫描功能
     */
    public void startBatchRfidScan() {
        startScannIntent = new Intent();
        startScannIntent.setAction(START_SCAN);
        mContext.sendBroadcast(startScannIntent);
    }

    public void stopBatchRfidScann() {
        if (mContext != null) {
            stopScannIntent = new Intent();
            stopScannIntent.setAction(STOP_SCAN);
            mContext.sendBroadcast(stopScannIntent);
        }
    }

    private BroadcastReceiver singleRifdReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (RFIDScanManager.this) {
                String action = intent.getAction();
                if (ACTION_SEND_BASE_EPC.equals(action)) {// 单扫
                    Bundle bundle = intent.getExtras();
                    String epc = bundle.getString("se4500");
                    Log.e("RFIDScanManager", "onRfidListener: epc -- " + epc);
                    if (rfidListener != null) {
                        inputFromBroadcast = true;
                        rfidListener.onRfidListener(epc);
                        inputFromBroadcast = false;
                    }
                }
            }
        }
    };

    private BroadcastReceiver batchRifdReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (RFIDScanManager.this) {
                String action = intent.getAction();
                if (ACTION_SEND_EPC.equals(action)) {
                    Bundle bundle = intent.getExtras();
                    String epc = bundle.getString("epc");
                    if (rfidListener != null) {
                        inputFromBroadcast = true;
                        rfidListener.onRfidListener(epc);
                        inputFromBroadcast = false;
                    }
                }
            }
        }
    };

    public void registerBatchRfid() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SEND_EPC);
        mContext.registerReceiver(batchRifdReciver, filter);
        isBatchRegister = true;
    }

    public void unregisterBatchRfid() {
        if (isBatchRegister) {
            mContext.unregisterReceiver(batchRifdReciver);
            isBatchRegister = false;
        }
    }

    public boolean isInputFromBroadcast() {
        return inputFromBroadcast;
    }

    /**
     * 定义一个接口
     */
    public interface RfidListener {

        void onRfidListener(String epc);
    }

    /**
     * 定义一个变量储存数据
     */
    private RfidListener rfidListener;

    /**
     * 提供公共的方法,并且初始化接口类型的数据
     */
    public void setOnRfidListener(RfidListener listener) {
        this.rfidListener = listener;
        Log.d("RFIDScanManager", "RFIDScanManager : setOnRfidListener " + listener);
    }

}
