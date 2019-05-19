package com.cmct.kettle.core.model.vo;


import com.cmct.kettle.core.model.po.KTrans;

import java.util.Date;

/**
 * @program: master
 * @description:
 * @author: fanyunxu
 * @create: 2019-05-17 15:19
 **/
public class TransVo extends KTrans {

    private String quartzCron ;
    private Integer monitorSuccess;
    private Integer monitorFail;
    private Date lastExTime;
    public TransVo() {
    }

    public String getQuartzCron() {
        return quartzCron;
    }

    public void setQuartzCron(String quartzCron) {
        this.quartzCron = quartzCron;
    }

    public Integer getMonitorSuccess() {
        return monitorSuccess;
    }

    public void setMonitorSuccess(Integer monitorSuccess) {
        this.monitorSuccess = monitorSuccess;
    }

    public Integer getMonitorFail() {
        return monitorFail;
    }

    public void setMonitorFail(Integer monitorFail) {
        this.monitorFail = monitorFail;
    }

    public Date getLastExTime() {
        return lastExTime;
    }

    public void setLastExTime(Date lastExTime) {
        this.lastExTime = lastExTime;
    }
}
