package com.phoenixhell.learnquartz.listener;


import com.phoenixhell.learnquartz.springQuartz.SpringQuartzJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * todo ApplicationListener 忘记了有空复习下
 * 创建一个监听器 监听 ContextRefreshedEvent事件：容器刷新完成（所有bean都完全创建）会发布这个事件；
 *
 * 也可以用@EventListener就不用创建这个类了 具体看下spring注解文档
 */

@Component
public class QuartzListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private Scheduler scheduler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            TriggerKey triggerKey1 = TriggerKey.triggerKey("trigger1", "triggerGroup1");
            Trigger trigger = scheduler.getTrigger(triggerKey1);
            //trigger不存在才新建一个 确保trigger1 group1 触发器在调度器里面是唯一的
            if(trigger==null){
                //注意JobDataMap 持久化对trigger 不起作用
                /**
                 * 秒 分 时 日 月 周
                 * <p>
                 * 日和周的位置随便谁是? 都行 冲突
                 *
                 * 任意事件开始每隔5秒就执行一次
                 */
                 trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey1)
                        .usingJobData("triggerKey","trigger1")
                        //.usingJobData("name","triggerSetName")
                        //.startNow()
                        .withSchedule(CronScheduleBuilder.cronSchedule("*/5 * * * * ?"))
                        .build();

                 //这边记得数据库表清空了
                JobDetail jobDetail = JobBuilder.newJob(SpringQuartzJob.class)
                        .withIdentity("job1", "JobGroup1")
                        .usingJobData("jobCount",0)
                        .requestRecovery(true) //scheduler 奔溃或者关机 重新启动该job会被重新执行
                        .build();

                scheduler.scheduleJob(jobDetail, trigger);
                //scheduler.start();
            }

            //============================集群 可以启动多个scheduler==============================
            //集群的意思就是说多个springboot 微服务 使用共同的数据库 创建job的意思  大概...

            TriggerKey triggerKey2 = TriggerKey.triggerKey("trigger2", "triggerGroup1");
            Trigger trigger2 = scheduler.getTrigger(triggerKey2);
            if(trigger2==null){
                trigger2 = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey2)
                        .usingJobData("triggerKey","trigger2")
                        //.usingJobData("name","triggerSetName")
                        //.startNow()
                        .withSchedule(CronScheduleBuilder.cronSchedule("*/5 * * * * ?"))
                        .build();

                JobDetail jobDetail2 = JobBuilder.newJob(SpringQuartzJob.class)
                        .withIdentity("job2", "JobGroup2")
                        .usingJobData("jobCount",0)
                        .requestRecovery(true) //scheduler 奔溃或者关机 重新启动该job会被重新执行
                        .build();

                scheduler.scheduleJob(jobDetail2, trigger2);
            }

            TriggerKey triggerKey3 = TriggerKey.triggerKey("trigger3", "triggerGroup3");
            Trigger trigger3 = scheduler.getTrigger(triggerKey3);
            if(trigger==null){
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey3)
                        .usingJobData("triggerKey","trigger3")
                        .withSchedule(CronScheduleBuilder.cronSchedule("*/5 * * * * ?"))
                        .build();

                //这边记得数据库表清空了
                JobDetail jobDetail3 = JobBuilder.newJob(SpringQuartzJob.class)
                        .withIdentity("job3", "JobGroup3")
                        .usingJobData("jobCount",0)
                        .requestRecovery(true) //scheduler 奔溃或者关机 重新启动该job会被重新执行
                        .build();

                scheduler.scheduleJob(jobDetail3, trigger3);

            }

            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
