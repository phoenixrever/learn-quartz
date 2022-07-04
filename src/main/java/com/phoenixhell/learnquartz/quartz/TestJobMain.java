package com.phoenixhell.learnquartz.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * spring boot 启动太慢了 main 方法来测了
 * <p>
 * Job：要由表示要执行的“作业”的类实现的接口。
 * <p>
 * JobDetail ：传递给定作业实例的详细信息属性。 JobDetails将使用JobBuilder创建/定义。
 * <p>
 * Trigger ：具有所有触发器通用属性的基本接口，描述了job执行的时间出发规则。
 * - 使用TriggerBuilder实例化实际触发器。
 * <p>
 * Scheduler ：这是Quartz Scheduler的主要接口，代表一个独立运行容器。
 * 调度程序维护JobDetails和触发器的注册表。
 * 一旦注册，调度程序负责执行作业，当他们的相关联的触发器触发（当他们的预定时间到达时）。
 *
 */
public class TestJobMain {
    public static void main(String[] args) throws SchedulerException {
        int count=0;
        //查看job执行了多少次
        JobDetail jobDetail = JobBuilder.newJob(MyJob.class)
                .withIdentity("myJob1", "myJobs")
                .usingJobData("jobKey","jobDetail")
                .usingJobData("jobCount",count)
                .usingJobData("name","jobDetailSetName")
                .requestRecovery(false) //scheduler 奔溃或者关机 重新启动该job会被重新执行
                .build();

        //注意JobDataMap 持久化对trigger 不起作用
        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("myJob1Trigger", "myJobsTrigger")
                .usingJobData("triggerKey","trigger")
                //.usingJobData("name","triggerSetName")
                .startNow()
                //.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).repeatForever())
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(2))
                .build();

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.start();
    }
}
