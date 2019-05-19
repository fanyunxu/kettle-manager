package com.cmct.kettle.web.service;

import com.cmct.kettle.common.toolkit.Constant;
import com.cmct.kettle.core.dto.BootTablePage;
import com.cmct.kettle.core.mapper.KJobMonitorDao;
import com.cmct.kettle.core.model.po.KJobMonitor;
import com.cmct.kettle.web.utils.CommonUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobMonitorService {

	@Autowired
	private KJobMonitorDao kJobMonitorDao;
	
	/**
	 * @Title getList
	 * @Description 获取作业监控分页列表
	 * @param start 起始行数
	 * @param size 每页数据条数
	 * @param uId 用户ID
	 * @return
	 * @return BootTablePage
	 */
	public BootTablePage getList(Integer start, Integer size, Integer uId){
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(uId);		
		template.setMonitorStatus(1);
		Page page = PageHelper.startPage(start,size);
		 kJobMonitorDao.select(template);
		return new BootTablePage(page);
	}
	
	/**
	 * @Title getList
	 * @Description 获取作业监控不分页列表
	 * @param uId 用户ID
	 * @return
	 * @return BootTablePage
	 */
	public BootTablePage getList(Integer uId){
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(uId);		
		template.setMonitorStatus(1);
		List<KJobMonitor> kJobMonitorList = kJobMonitorDao.select(template);
		Collections.sort(kJobMonitorList);  
		List<KJobMonitor> newKJobMonitorList = new ArrayList<KJobMonitor>();
		if (kJobMonitorList.size() >= 5){
			newKJobMonitorList = kJobMonitorList.subList(0, 5);	
		}
		BootTablePage bootTablePage = new BootTablePage();
		bootTablePage.setRows(newKJobMonitorList);
		bootTablePage.setTotal(5);
		return bootTablePage;
	}
	
	/**
	 * @Title getAllMonitorJob
	 * @Description 获取所有的监控作业
	 * @param uId 用户ID
	 * @return
	 * @return Integer
	 */
	public Integer getAllMonitorJob(Integer uId){
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(uId);		
		template.setMonitorStatus(1);
		List<KJobMonitor> kJobMonitorList = kJobMonitorDao.select(template);
		return kJobMonitorList.size();
	}
	
	/**
	 * @Title getAllSuccess
	 * @Description 获取执行成功的数
	 * @param uId 用户ID
	 * @return
	 * @return Integer
	 */
	public Integer getAllSuccess(Integer uId){
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(uId);		
		template.setMonitorStatus(1);
		List<KJobMonitor> kJobMonitorList = kJobMonitorDao.select(template);
		Integer allSuccess = 0;
		for (KJobMonitor KJobMonitor : kJobMonitorList){
			allSuccess += KJobMonitor.getMonitorSuccess();
		}
		return allSuccess;
	}
	
	/**
	 * @Title getAllFail
	 * @Description 获取执行失败的数
	 * @param uId 用户ID
	 * @return
	 * @return Integer
	 */
	public Integer getAllFail(Integer uId){
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(uId);		
		template.setMonitorStatus(1);
		List<KJobMonitor> kJobMonitorList = kJobMonitorDao.select(template);
		Integer allSuccess = 0;
		for (KJobMonitor KJobMonitor : kJobMonitorList){
			allSuccess += KJobMonitor.getMonitorFail();
		}
		return allSuccess;
	}
	
	/**
	 * @Title getTransLine
	 * @Description 获取7天内作业的折线图
	 * @param uId 用户ID
	 * @return
	 * @return Map<String,Object>
	 */
	public Map<String, Object> getJobLine(Integer uId){
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(uId);		
		List<KJobMonitor> kJobMonitorList = kJobMonitorDao.select(template);
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		List<Integer> resultList = new ArrayList<Integer>();
		for (int i = 0; i < 7; i++){
			resultList.add(i, 0);
		}
		if (kJobMonitorList != null && !kJobMonitorList.isEmpty()){
			for (KJobMonitor KJobMonitor : kJobMonitorList){
				String runStatus = KJobMonitor.getRunStatus();
				if (runStatus != null && runStatus.contains(",")){
					String[] startList = runStatus.split(",");
					for (String startOnce : startList){
						String[] startAndStopTime = startOnce.split(Constant.RUNSTATUS_SEPARATE);
						if(startAndStopTime.length!=2)
							continue;
						//得到一次任务的起始时间和结束时间的毫秒值
						resultList = CommonUtils.getEveryDayData(Long.parseLong(startAndStopTime[0]), Long.parseLong(startAndStopTime[1]), resultList);
					}
				}			
			}	
		}		
		resultMap.put("name", "作业");
		resultMap.put("data", resultList);
		return resultMap;
	}
}