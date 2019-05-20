package io.fanyun.kettle.web.controller;

import io.fanyun.kettle.base.BaseWhere;
import io.fanyun.kettle.common.toolkit.Constant;
import io.fanyun.kettle.core.dto.BootTablePage;
import io.fanyun.kettle.core.model.po.KUser;
import io.fanyun.kettle.web.service.JobMonitorService;
import io.fanyun.kettle.web.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/job/monitor/")
public class JobMonitorController {
	
	@Autowired
	private JobMonitorService jobMonitorService;
	
	@RequestMapping("getList.shtml")
	public String getList(BaseWhere where, HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		BootTablePage list = jobMonitorService.getList(where, kUser.getuId());
		return JsonUtils.objectToJson(list);
	}
	
	@RequestMapping("getAllMonitorJob.shtml")
	public String getAllMonitorJob(HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		return JsonUtils.objectToJson(jobMonitorService.getAllMonitorJob(kUser.getuId()));
	}
	
	@RequestMapping("getAllSuccess.shtml")
	public String getAllSuccess(HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		return JsonUtils.objectToJson(jobMonitorService.getAllSuccess(kUser.getuId()));
	}
	
	@RequestMapping("getAllFail.shtml")
	public String getAllFail(HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		return JsonUtils.objectToJson(jobMonitorService.getAllFail(kUser.getuId()));
	}
}
