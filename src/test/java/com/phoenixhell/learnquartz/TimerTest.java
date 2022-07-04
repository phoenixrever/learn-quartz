package com.phoenixhell.learnquartz;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * test 不能执行定时任务的，原因我忘记了，也不想查
 *
 * 总结来说就是  timer.schedule 是单线程执行任务阻塞
 * 解决犯法就是在run 方法里面 异步执行
 */
public class TimerTest {
    public static void main(String[] args) {
        //此时定时任务已经启动
        Timer timer = new Timer();
        for (int i = 0; i < 2; i++) {
        //添加定时任务
        //nextExecuteTime 预设的下一个任务执行间隔
        //schedule 真正执行时间上一个任务结束之后开始 有可能会丢任务
        /**
         *  TimeUnit.SECONDS.sleep(3000);  3秒超出任务执行间隔 就会丢弃下一次任务
         */
        MyTimerTask myTimerTask = new MyTimerTask("task--" );
        timer.schedule(myTimerTask,0,2000);

        //scheduleAtFireRate 不管上一个任务结束时间 每隔2秒执行任务 有可能提前执行任务(上一个任务还没执行完成)
        //timer.scheduleAtFixedRate(myTimerTask,0,2000);


        }
    }

    static class MyTimerTask extends TimerTask {
        private  String taskName;

        public MyTimerTask(String taskName) {
            this.taskName=taskName;
        }

        @Override
        public void run() {
            //在此启动新线程执行
            try {
                System.out.println("start-----" + taskName + "------" + new Date());
                TimeUnit.SECONDS.sleep(3);
                System.out.println("end-----" + taskName + "------" + new Date());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

