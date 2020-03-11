package com.dc.top.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ author:: helei
 * @ created on: 18/6/26 下午3:55
 * @ description:
 */
public class ThreadManagerUtils {

    public static int CORE_POOL_SIZE = 30;

    public static ThreadPool getInstance() {
        return Holder.mInstance;
    }

    private static class Holder {

        public final static ThreadPool mInstance = new ThreadPool(CORE_POOL_SIZE);
    }

    public static class ThreadPool {

        private ExecutorService mExecutor;

        private ThreadPool(int corePoolSize) {
            mExecutor = Executors.newFixedThreadPool(corePoolSize);
        }

        public void execute(Runnable runnable) {
            if (runnable == null) {
                return;
            }
            mExecutor.execute(runnable);
        }
    }

}
