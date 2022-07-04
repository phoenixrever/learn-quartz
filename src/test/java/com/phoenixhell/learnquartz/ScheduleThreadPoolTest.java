package com.phoenixhell.learnquartz;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务线程池
 */
public class ScheduleThreadPoolTest {
    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorPool = Executors.newScheduledThreadPool(5);
        //只执行一次，没有间隔时间
        //scheduledExecutorPool.schedule(new MyRunnable(),2000,  TimeUnit.SECONDS);

        scheduledExecutorPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

            }
        }, 0, 2, TimeUnit.SECONDS);
    }
}

