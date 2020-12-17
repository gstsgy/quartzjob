package com.sr.suray.quartzjob.util;

import com.sr.suray.quartzjob.bean.TaskInfo;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @ClassName QuartzManager
 * @Description TODO
 * @Author guyue
 * @Date 2020/9/4 下午5:33
 **/
@Service
public class QuartzManager {

    @Autowired
    private Scheduler scheduler;

    /**
     * 添加任务
     *
     * @param task
     * @throws SchedulerException
     */
    @SuppressWarnings("unchecked")
    public void addJob(TaskInfo task) {
        try {
           // Class<? extends Job>  job =(Class<? extends Job>) new HelloWorldJob(task.getTargeturl());
            JobDetail jobDetail = JobBuilder.newJob(HelloWorldJob.class).withIdentity(task.getJobName())
                    .usingJobData("url",task.getTargeturl())
                    // 任务名称和组构成任务key
                    .build();
            // 定义调度触发规则
            // 使用cornTrigger规则
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(task.getJobName())// 触发器key
                    .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.SECOND))
                    .withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpression())).startNow().build();
            // 把作业和触发器注册到任务调度中
            scheduler.scheduleJob(jobDetail, trigger);
            // 启动
            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取所有计划中的任务列表
     *
     * @return
     * @throws SchedulerException
     */
    public List<TaskInfo> getAllJob() throws SchedulerException {
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        List<TaskInfo> jobList = new ArrayList<TaskInfo>();
        for (JobKey jobKey : jobKeys) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers) {
                TaskInfo job = new TaskInfo();
                job.setJobName(jobKey.getName());
                //job.set(jobKey.getGroup());
                job.setJobDesc("触发器:" + trigger.getKey());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                job.setJobStatus(Integer.parseInt(triggerState.name()));
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    job.setCronExpression(cronExpression);
                }
                jobList.add(job);
            }
        }
        return jobList;
    }

    /**
     * 所有正在运行的job
     *
     * @return
     * @throws SchedulerException
     */
    public List<TaskInfo> getRunningJob() throws SchedulerException {
        List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
        List<TaskInfo> jobList = new ArrayList<TaskInfo>(executingJobs.size());
        for (JobExecutionContext executingJob : executingJobs) {
            TaskInfo job = new TaskInfo();
            JobDetail jobDetail = executingJob.getJobDetail();
            JobKey jobKey = jobDetail.getKey();
            Trigger trigger = executingJob.getTrigger();
            job.setJobName(jobKey.getName());
           // job.setJobGroup(jobKey.getGroup());
            job.setJobDesc("触发器:" + trigger.getKey());
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            job.setJobStatus(Integer.parseInt(triggerState.name()));
            if (trigger instanceof CronTrigger) {
                CronTrigger cronTrigger = (CronTrigger) trigger;
                String cronExpression = cronTrigger.getCronExpression();
                job.setCronExpression(cronExpression);
            }
            jobList.add(job);
        }
        return jobList;
    }

    /**
     * 暂停一个job
     *
     * @param task
     * @throws SchedulerException
     */
    public void pauseJob(TaskInfo task) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(task.getJobName());
        scheduler.pauseJob(jobKey);
    }

    /**
     * 恢复一个job
     *
     * @param task
     * @throws SchedulerException
     */
    public void resumeJob(TaskInfo task) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(task.getJobName());
        scheduler.resumeJob(jobKey);
    }

    /**
     * 删除一个job
     *
     * @param task
     * @throws SchedulerException
     */
    public void deleteJob(TaskInfo task) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(task.getJobName());
        scheduler.deleteJob(jobKey);

    }

    /**
     * 立即执行job
     *
     * @param task
     * @throws SchedulerException
     */
    public void runJobNow(TaskInfo task) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(task.getJobName());
        scheduler.triggerJob(jobKey);
    }

    /**
     * 更新job时间表达式
     *
     * @param task
     * @throws SchedulerException
     */
    public void updateJobCron(TaskInfo task) throws SchedulerException {

        TriggerKey triggerKey = TriggerKey.triggerKey(task.getJobName());

        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCronExpression());

        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

        scheduler.rescheduleJob(triggerKey, trigger);
    }
}
