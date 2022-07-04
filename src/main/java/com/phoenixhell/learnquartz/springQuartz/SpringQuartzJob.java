package com.phoenixhell.learnquartz.springQuartz;

import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Job：要由表示要执行的“作业”的类实现的接口。
 * <p>
 * DisallowConcurrentExecution   禁止并发的执行同一个job(jobDetail定义) 的多个实例
 * <p>
 * PersistJobDataAfterExecution  持久化jobDetail中的jobDataMap (对trigger中的JobDataMap)无无效
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class SpringQuartzJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            TimeUnit.SECONDS.sleep(2);
            System.out.println("schedule 调度器ID----"+context.getScheduler().getSchedulerInstanceId());
            System.out.println("jobDetail 任务名字----"+context.getJobDetail().getKey().getName());
            System.out.println("执行时间----"+simpleDateFormat.format(new Date()));

            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            int jobCount = jobDataMap.getInt("jobCount") + 1;
            jobDataMap.put("jobCount", jobCount);
            System.out.println("jobCount："+jobDataMap.getInt("jobCount"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
