package com.phoenixhell.learnquartz.quartz;

import org.quartz.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *  Job：要由表示要执行的“作业”的类实现的接口。
 *
 *  DisallowConcurrentExecution   禁止并发的执行同一个job(jobDetail定义) 的多个实例
 *
 *  PersistJobDataAfterExecution  持久化jobDetail中的jobDataMap (对trigger中的JobDataMap)无无效
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class MyJob implements Job {
    private String name;

    //usingJobData 如果key和属性相同 会自动调用此方法赋值
    public void setName(String name) {
        this.name = name;
    }


    //context 获取 usingJobData 存入的值
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());

        System.out.println("my first execute ---- "+ date);

        //JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        //JobDataMap triggerMap = context.getTrigger().getJobDataMap();
        //System.out.println("获取的jobMapData的值："+jobDataMap.get("jobKey"));
        //System.out.println("triggerMap："+triggerMap.get("triggerKey"));

        System.out.println("------------------------------------");
        //2个map 合到一起
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        System.out.println(mergedJobDataMap.getString("jobKey"));
        System.out.println(mergedJobDataMap.get("triggerKey"));

        System.out.println("------------------------------------");
        //usingJobData("name","triggerSetName") ；
        // 里面设置同名name 会自动调用setName方法
        // jobDataMap triggerMap 都会设置后面覆盖前面的
        System.out.println("name----------"+name);

        //jobDetail 每次都是一个新的实例，不会有阻塞问题
        System.out.println("\n");
        System.out.println("--------------MyJob每次都会====生成一个新的实例--------------------");
        System.out.println("jobDetail------>"+System.identityHashCode(context.getJobDetail()));
        System.out.println("job------>"+System.identityHashCode(context.getJobInstance()));

        //当然这样同时也有一个问题前一个任务没有执行完成，后面的又开始执行了
        // 我们不希望这样 可以用@DisallowConcurrentExecution 注释job
        // 这样 就会等待3秒执行一次(前面的任务执行完成)，而不是withSchedule 里面定义的2秒
        //不过注意 此时创建jobDetail实例 还是新建的，只不过是不运行上一个jobDetail没运行完成再创建新的而已
        try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }

        System.out.println("--------------p取出count的值 加1----------------------");
        //每次执行重triggerMap取出count的值 加1 然后放会triggerMap，不过这时候JobDataMap 会有多份每次都是新值
        //@PersistJobDataAfterExecution 注解加上后就能持久化 JobDataMap 会保原有的值
        //对trigger中的JobDataMap无效
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        int jobCount = jobDataMap.getInt("jobCount") + 1;
        jobDataMap.put("jobCount", jobCount);
        System.out.println("jobCount："+jobDataMap.getInt("jobCount"));
    }
}
