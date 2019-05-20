package io.fanyun.kettle.core.model.vo;

import java.util.Date;

public class QuartzVo {
    private String jobName;
    private String jobGroup;
    private Date nextFireTime;

    public QuartzVo(String jobName, String jobGroup, Date nextFireTime) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.nextFireTime = nextFireTime;
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
}
