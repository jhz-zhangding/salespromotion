package com.efrobot.salespromotion.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager {
    private ThreadManager() {
    }

    private static ThreadManager instance = new ThreadManager();
    private ThreadPoolProxy longPool;
    private ThreadPoolProxy shortPool;
    public static final int  MAX_COUNT= 5;
    public static ThreadManager getInstance() {
        return instance;
    }

    // 联网比较耗时
    public synchronized ThreadPoolProxy createLongPool() {
        if (longPool == null) {
            longPool = new ThreadPoolProxy(MAX_COUNT, MAX_COUNT, 5000L);
        }
        return longPool;
    }

    // 操作本地文件
    public synchronized ThreadPoolProxy createShortPool() {
        if (shortPool == null) {
            shortPool = new ThreadPoolProxy(3, 3, 5000L);
        }
        return shortPool;
    }

    public class ThreadPoolProxy {
        private ThreadPoolExecutor pool;

        public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long time) {
            // 创建线程池
                /*
                 * 1. 线程池里面管理多少个线程2. 如果排队满了, 额外的开的线程数3. 如果线程池没有要执行的任务 存活多久4.
				 * 时间的单位 5 如果 线程池里管理的线程都已经用了,剩下的任务 临时存到LinkedBlockingQueue对象中 排队
				 */
            pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                    time, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE));
        }

        public void execute(Runnable runnable) {

            pool.execute(runnable);
        }

        public void cancel(Runnable runnable) {
            if (pool != null && !pool.isShutdown() && !pool.isTerminated()) {
                pool.remove(runnable);
            }
        }

        public boolean contains(Runnable runnable) {
            LinkedBlockingQueue<Runnable> mQueue = (LinkedBlockingQueue<Runnable>) pool.getQueue();
            return mQueue.contains(runnable);
        }

        public void remove(Runnable runnable) {
            LinkedBlockingQueue<Runnable> mQueue = (LinkedBlockingQueue<Runnable>) pool.getQueue();
            if (mQueue != null) {
                mQueue.remove(runnable);
            }
        }

        public long getTaskCount() {
            long count=0;
            if(pool!=null){
                count=pool.getTaskCount();
            }
            return count;
        }
    }
}
