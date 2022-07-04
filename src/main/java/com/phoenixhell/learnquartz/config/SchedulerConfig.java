package com.phoenixhell.learnquartz.config;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;


//StdSchedulerFactory 读取classpath下配置文件中的配置实例化  Scheduler
@Configuration
public class SchedulerConfig {

    /**
     * 不用忘记 导入 spring-boot-starter-jdbc 才有datasource
     * mybatisPlus 自动帮你导了
     */
    @Autowired
    private DataSource dataSource;

    //renren fast也没有这个 亲测不需要这个
    //@Bean
    //public Scheduler scheduler() throws IOException {
    //    Scheduler scheduler = schedulerFactoryBean().getScheduler();
    //    return scheduler;
    //}

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        System.out.println(dataSource);
        factory.setDataSource(dataSource);

        //quartz参数
        Properties prop = new Properties();

        prop.put("org.quartz.scheduler.instanceName", "RenrenScheduler");//#集群名字
        prop.put("org.quartz.scheduler.instanceId", "AUTO"); //自动给集群中的节点分配一个唯一ID
        //线程池配置
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        prop.put("org.quartz.threadPool.threadCount", "20"); //线程数量
        prop.put("org.quartz.threadPool.threadPriority", "5"); //#线程的优先级,取值在Thread.MINPRIORITY(1)到Thread.MAXPRIORITY(10) 默认5
        //JobStore配置
        //renren-fast 是多数据源配置了 org.springframework.scheduling.quartz.LocalDataSourceJobStore
        //一般项目org.quartz.impl.jdbcjobstore.JobStoreTX  数据保存方式为数据库持久化

        //SpringBoot 2.5.7以上版本Quartz无法启动的问题
        //将yaml配置文件中Quartz下jobstore中的class属性注释掉，数据源就可以正常进行自动匹配了
        //默认Using job-store 'org.springframework.scheduling.quartz.LocalDataSourceJobStore' - which supports persistence. and is clustered.
        //prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");


        //集群配置
        prop.put("org.quartz.jobStore.isClustered", "true");
        //心跳检查集群是否在线 单位 ms
        prop.put("org.quartz.jobStore.clusterCheckinInterval", "15000");
        //每次可以执行的最大misfired的触发器数.如果设置很大的话,数据库在一瞬间就会锁住,性能将会受到很大的影响
        prop.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1");

        // job到达触发时间没有被执行 misfireThreshold  这个参数一般在多线程池条件下无效。
        //scheduler可以忍受未被调度的时间与下一次执行的时间的阈值,如果超出这个阈值,就不会被重新调用,默认一分钟,单位毫秒
        prop.put("org.quartz.jobStore.misfireThreshold", "12000");
        prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");

        //在“LOCKS”表里选择一行并且锁住这行的SQL语句。缺省的语句能够为大部分数据库工作。“{0}”是在运行时你配置的表前缀。
        //默认值 "SELECT * FROM {0}LOCKS WHERE LOCK_NAME = ? FOR UPDATE"
        //todo 一般默认就好 renren-fast 里面还没研究 自己项目应该不需要 注释掉
        //prop.put("org.quartz.jobStore.selectWithLockSQL", "SELECT * FROM {0}LOCKS UPDLOCK WHERE LOCK_NAME = ?");

        //PostgreSQL数据库，需要打开此注释
        //prop.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");

        factory.setQuartzProperties(prop);

        factory.setSchedulerName("clusteredScheduler");
        //延时启动 单位秒
        factory.setStartupDelay(3);
        //给ioc容器中的factory 取个名字
        factory.setApplicationContextSchedulerContextKey("applicationContextKey");
        //可选，QuartzScheduler 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        factory.setOverwriteExistingJobs(true);
        //设置自动启动，默认为true
        factory.setAutoStartup(true);
        //renren 没有设置这个  Using default implementation for ThreadExecutor
        //factory.setTaskExecutor();

        return factory;
    }

    //从yaml读取properties 还是不在yaml设置 一来是没有提示  二是直接写在factory 里面方便
    //@Bean
    //public Properties properties() throws IOException {
    //    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    //    propertiesFactoryBean.setLocation(new ClassPathResource("/spring-quartz.properties"));
    //    propertiesFactoryBean.afterPropertiesSet();
    //    return propertiesFactoryBean.getObject();
    //}
}
