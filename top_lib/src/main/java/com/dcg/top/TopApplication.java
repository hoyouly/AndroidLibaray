package com.dcg.top;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class TopApplication extends Application {
    //初始化运行在UI线程中的handler
    public static Handler UIHandler = new Handler(Looper.getMainLooper());
    private static Context ctx;

    public static Context getAppContext() {
        return ctx;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = getApplicationContext();
    }
}
