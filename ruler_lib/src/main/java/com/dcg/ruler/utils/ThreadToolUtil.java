package com.dcg.ruler.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * author: Severn
 * date: 2020/4/14
 * description: 线程池工具
 * email: shiszb@digitalchina.com
 */
public class ThreadToolUtil {

    private final Executor mDiskIO;

    private final Executor mNetworkIO;

    private final Executor mMainThread;
    private final ScheduledThreadPoolExecutor schedule;

    private static ThreadToolUtil instance;

    private static Object object = new Object();

    public static ThreadToolUtil getInstance() {
        if (instance == null) {
            synchronized (object) {
                if (instance == null) {
                    instance = new ThreadToolUtil();
                }
            }
        }
        return instance;
    }

    private ThreadToolUtil() {

        this.mDiskIO = Executors.newSingleThreadExecutor(new MyThreadFactory("single"));

        this.mNetworkIO = Executors.newFixedThreadPool(3, new MyThreadFactory("fixed"));

        this.mMainThread = new MainThreadExecutor();

        this.schedule = new ScheduledThreadPoolExecutor(5, new MyThreadFactory("sc"), new ThreadPoolExecutor.AbortPolicy());
    }

    class MyThreadFactory implements ThreadFactory {

        private final String name;
        private int count = 0;

        MyThreadFactory(String name) {
            this.name = name;
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            count++;
            return new Thread(r, name + "-" + count + "-Thread");
        }
    }

    public Executor diskIO() {
        return mDiskIO;
    }

    public ScheduledThreadPoolExecutor schedule() {
        return schedule;
    }

    public Executor networkIO() {
        return mNetworkIO;
    }

    public Executor mainThread() {
        return mMainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

}
