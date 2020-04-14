package com.dcg.network.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @ Time :  2019-12-27
 * @ Author :  helei
 * @ Email :  heleik@digitalchina.com
 * @ Description :
 */
public class GsonManager {

    private Gson mGson;

    private GsonManager() {
        mGson = new Gson();
    }

    private static GsonManager mSingleton = null;

    public static GsonManager getInstance() {
        if (mSingleton == null) {
            synchronized (GsonManager.class) {
                if (mSingleton == null) {
                    mSingleton = new GsonManager();
                }
            }
        }
        return mSingleton;
    }


    public <T> T fromJson(String json, Class<T> tClass) {
        return mGson.fromJson(json, tClass);
    }

    public <T> T fromJson(String json, Type typeOfT) {
        return mGson.fromJson(json, typeOfT);
    }


    public String toJson(Object src) {
        return mGson.toJson(src);
    }
}
