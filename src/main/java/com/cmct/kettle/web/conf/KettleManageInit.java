package com.cmct.kettle.web.conf;

import com.cmct.kettle.core.mapper.KTransDao;
import com.cmct.kettle.core.model.po.KTrans;
import com.cmct.kettle.web.service.TransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: master
 * @description:
 * @author: fanyunxu
 * @create: 2019-05-17 14:03
 **/
@Component
public  class KettleManageInit implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private  TransService transService;
    @Autowired
    private  KTransDao kTransDao;

    public   void startTrans() {
        List<Integer> transIds= getNeedStartTrans();
        transIds.forEach(t->{
            transService.start(t);
        });
    }

    /**
     * 获取需要启动的任务
     * @return
     */
    private  List<Integer> getNeedStartTrans() {
        KTrans kTrans=new KTrans();
        kTrans.setDelFlag(1);
        kTrans.setTransStatus(1);
        List<KTrans> trans=  kTransDao.select(kTrans);
        if(trans==null||trans.size()==0){
            return new ArrayList<>();
        }
        return trans.stream().map(KTrans::getTransId).collect(Collectors.toList());
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        startTrans();
    }
}
