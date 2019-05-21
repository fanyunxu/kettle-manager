package io.fanyun.kettle.core.model.vo;

import java.util.Date;

public class QuartzVo {
    private String jobName;
    private String jobGroup;
    private Date nextFireTime;
    /**
     * 0 未执行 1运行
     */
    private Integer executing;
    public QuartzVo(String jobName, String jobGroup, Date nextFireTime,Integer executing) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.nextFireTime = nextFireTime;
        this.executing=executing;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Integer getExecuting() {
        return executing;
    }

    public void setExecuting(Integer executing) {
        this.executing = executing;
    }
}
