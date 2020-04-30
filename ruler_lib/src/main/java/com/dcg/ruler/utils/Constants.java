package com.dcg.ruler.utils;

/**
 * author: Severn
 * date: 2020/4/26
 * email: shiszb@digitalchina.com
 * description: 原始版本发布的事件 EventBus.getDefault().post(
 */
public class Constants {
    // 广播接收者触发 👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇
    public static final String KP_TRUE  = "KP-TRUE";
    public static final String KP_FALSE  = "KP-FALSE";
    public static final String LWHDATA_LWH  = "LWHDATA-LWH";
    public static final String SAVE6DATA_SUCCESS = "SAVE6DATA-SUCCESS";
    public static final String SAVE6DATA_DATAERR  = "SAVE6DATA-DATAERR";
    public static final String SERVICECONNECTEDSTATUS_TRUE  = "SERVICECONNECTEDSTATUS-TRUE";
    public static final String SERVICECONNECTEDSTATUS_FALSE  = "SERVICECONNECTEDSTATUS-FALSE";

    // 用户手动的触发 👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇
    public static final String NOTIFICATION_SUCCESS  = "NOTIFICATION_SUCCESS";
    public static final String NOTIFICATION_FAILURE  = "NOTIFICATION-FAILURE";
    public static final String NOTIFICATION_TMF  = "NOTIFICATION-TOO-MANY-FAILURES";
    public static final String NOTIFICATION_PEVS  = "NOTIFICATION-PLEASE-ENTER-VALID-SCALE";
    public static final String NOTIFICATION_PCTD  = "NOTIFICATION-PLEASE-CONNECT-THE-DEVICE";
    public static final String NOTIFICATION_SET_SUCCESSFULLY  = "NOTIFICATION-SET-SUCCESSFULLY";

}
