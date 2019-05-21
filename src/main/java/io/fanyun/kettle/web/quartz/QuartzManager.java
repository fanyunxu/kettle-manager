package io.fanyun.kettle.web.quartz;

import io.fanyun.kettle.common.toolkit.ExecuteStatus;
import io.fanyun.kettle.core.model.vo.QuartzVo;
import io.fanyun.kettle.web.utils.ApplicationContextProviderUtil;
import io.fanyun.kettle.web.utils.DateTime;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class QuartzManager {

	private  Scheduler sched;

    public QuartzManager(Scheduler sched) {
        this.sched = sched;
    }

    /**
	 * @Title addJob
	 * @Description 添加一个定时任务 
	 * @param jobName 任务名
	 * 				以作业为例：  JOB@1(资源库ID)@/job/mysql-mysql(JOB全路径)
	 * @param jobGroupName 任务组名 
	 * 				以作业为例：  JOB_GROUP@1(用户ID)@1(资源库ID)@/job/mysql-mysql(JOB全路径)
	 * @param triggerName 触发器名 
	 * 				以作业为例：  TRIGGER@1(资源库ID)@/job/mysql-mysql(JOB全路径)
	 * @param triggerGroupName 触发器组名 
	 * 				以作业为例：  TRIGGER_GROUP@1(用户ID)@1(资源库ID)@/job/mysql-mysql(JOB全路径)
	 * @param jobClass 任务对象实例
	 * @param cron 时间设置，参考quartz说明文档 
	 * @param parameter 传入的参数
	 * @return void
	 */
    public  void addJob(String jobName, String jobGroupName, 
            String triggerName, String triggerGroupName, Class<? extends Job> jobClass, String cron, Map<String, Object> parameter) {  
        try {
            // 任务名，任务组，任务执行类
            JobDetail jobDetail= JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
            // 添加任务执行的参数
            parameter.forEach((k, v) -> {
            	jobDetail.getJobDataMap().put(k, v);
            });            
            // 触发器
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            // 触发器名,触发器组
            triggerBuilder.withIdentity(triggerName, triggerGroupName);
            triggerBuilder.startNow();
            // 触发器时间设定
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
            // 创建Trigger对象
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();
            // 调度容器设置JobDetail和Trigger
            sched.scheduleJob(jobDetail, trigger);
            // 启动  
            if (!sched.isShutdown()) {  
                sched.start();  
            }  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  
    
    
    public  void addOnceJob(String jobName, String jobGroupName, 
            String triggerName, String triggerGroupName, Class<? extends Job> jobClass, Map<String, Object> parameter){
    	try {
    		// 任务名，任务组，任务执行类
            JobDetail jobDetail= JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
            // 添加任务执行的参数
            parameter.forEach((k, v) -> {
            	jobDetail.getJobDataMap().put(k, v);
            });
            // 触发器
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            // 触发器名,触发器组
            triggerBuilder.withIdentity(triggerName, triggerGroupName);
            triggerBuilder.startNow();
            //立即执行
            StringBuilder cronBuilder = new StringBuilder();
    		DateTime dateTime = new DateTime();
    		Integer addMinute = dateTime.second() >= 58 ? 2 : 1;
    		cronBuilder.append("0").append(" ")
    						.append(dateTime.minute() + addMinute).append(" ")
    						.append(dateTime.hour(true)).append(" ")
    						.append(dateTime.dayOfMonth()).append(" ")
    						.append(dateTime.monthStartFromOne()).append(" ")
    						.append("?").append(" ")
    						.append(String.valueOf(dateTime.year()));
            // 触发器时间设定
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cronBuilder.toString()));
            // 创建Trigger对象
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();
            // 调度容器设置JobDetail和Trigger
            sched.scheduleJob(jobDetail, trigger);
            // 添加任务执行监听器
            sched.getListenerManager().addJobListener( ApplicationContextProviderUtil.getBean(QuartzListener.class),
            		KeyMatcher.keyEquals(new JobKey(jobName, jobGroupName)));
            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 
     * @Description: 修改一个任务的触发时间
     *  
     * @param jobName 
     * @param jobGroupName
     * @param triggerName 触发器名
     * @param triggerGroupName 触发器组名 
     * @param cron   时间设置，参考quartz说明文档   
     */  
    public  void modifyJobTime(String jobName, 
            String jobGroupName, String triggerName, String triggerGroupName, String cron) {  
        try {  
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);  
            if (trigger == null) {  
                return;  
            }
            String oldTime = trigger.getCronExpression();  
            if (!oldTime.equalsIgnoreCase(cron)) { 
                /** 方式一 ：调用 rescheduleJob 开始 */
                // 触发器  
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
                // 触发器名,触发器组  
                triggerBuilder.withIdentity(triggerName, triggerGroupName);
                triggerBuilder.startNow();
                // 触发器时间设定  
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
                // 创建Trigger对象
                trigger = (CronTrigger) triggerBuilder.build();
                // 方式一 ：修改一个任务的触发时间
                sched.rescheduleJob(triggerKey, trigger);
                /** 方式一 ：调用 rescheduleJob 结束 */

                /** 方式二：先删除，然后在创建一个新的Job  */
                //JobDetail jobDetail = sched.getJobDetail(JobKey.jobKey(jobName, jobGroupName));  
                //Class<? extends Job> jobClass = jobDetail.getJobClass();  
                //removeJob(jobName, jobGroupName, triggerName, triggerGroupName);  
                //addJob(jobName, jobGroupName, triggerName, triggerGroupName, jobClass, cron); 
                /** 方式二 ：先删除，然后在创建一个新的Job */
            }  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  

    /** 
     * @Description: 移除一个任务 
     *  
     * @param jobName 
     * @param jobGroupName 
     * @param triggerName 
     * @param triggerGroupName 
     */  
    public  void removeJob(String jobName, String jobGroupName,  
            String triggerName, String triggerGroupName) {  
        try {  
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            // 停止触发器
            sched.pauseTrigger(triggerKey);
            // 移除触发器
            sched.unscheduleJob(triggerKey);
            // 删除任务
            sched.deleteJob(JobKey.jobKey(jobName, jobGroupName));
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }

    public  List<QuartzVo> getAllJobs(){
        List<QuartzVo> quartzVos=new ArrayList<>();
        List<String> executing=getAllExecutingJob();
        try {
            for (String groupName : sched.getJobGroupNames()) {
                for (JobKey jobKey : sched.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();
                    List<Trigger> triggers = (List<Trigger>) sched.getTriggersOfJob(jobKey);
                    Integer executeStatus=executing.contains(jobName)?ExecuteStatus.YES.ordinal():ExecuteStatus.NO.ordinal();
                    QuartzVo quartzVo=new QuartzVo(jobName,jobGroup,triggers.get(0).getNextFireTime(),executeStatus);
                    quartzVos.add(quartzVo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return quartzVos;
    }

    /**
     * 获取所有正在执行的任务
     * @return
     */
    public List<String> getAllExecutingJob(){
        List<String> res=new ArrayList<>();
        try {
            List<JobExecutionContext>  jobExecutionContexts=sched.getCurrentlyExecutingJobs();
            for (JobExecutionContext jobExecutionContext : jobExecutionContexts) {
                res.add(jobExecutionContext.getTrigger().getJobKey().getName()) ;
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return res;
    }
    /** 
     * @Description:启动所有定时任务 
     */  
    public  void startJobs() {  
        try {  
            sched.start();
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  

    /** 
     * @Description:关闭所有定时任务 
     */  
    public  void shutdownJobs() {  
        try {  
            if (!sched.isShutdown()) {
                sched.shutdown();  
            }  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    } 
}
