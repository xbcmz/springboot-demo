package com.haoxs.delay.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;


/**
 * 数据库轮询-quartz
 *
 * 优点:简单易行，支持集群操作
 * 缺点:
 * (1)对服务器内存消耗大
 * (2)存在延迟，比如你每隔3分钟扫描一次，那最坏的延迟时间就是3分钟
 * (3)假设你的订单有几千万条，每隔几分钟这样扫描一次，数据库损耗极大
 *
 * @author Mr.
 * @Date 2021-09-18
 */
public class MyJob implements Job {

    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        System.out.println("要去数据库扫描啦。。。");
    }

    public static void main(String[] args) throws Exception {
        // 创建任务
        JobDetail jobDetail = JobBuilder.newJob(MyJob.class)
                .withIdentity("job1", "group1").build();
        // 创建触发器 每3秒钟执行一次
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("trigger1", "group3")
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(3).repeatForever())
                .build();
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        // 将任务及其触发器放入调度器
        scheduler.scheduleJob(jobDetail, trigger);
        // 调度器开始调度任务
        scheduler.start();
    }
}
