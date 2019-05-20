package io.fanyun.kettle.web.controller;

import io.fanyun.kettle.base.BaseWhere;
import io.fanyun.kettle.common.toolkit.Constant;
import io.fanyun.kettle.core.dto.BootTablePage;
import io.fanyun.kettle.core.model.po.KUser;
import io.fanyun.kettle.web.service.TransMonitorService;
import io.fanyun.kettle.web.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/trans/monitor/")
public class TransMonitorController {
	
	@Autowired
	private TransMonitorService transMonitorService;
	
	@RequestMapping("getList.shtml")
	public String getList(BaseWhere where, HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		BootTablePage list = transMonitorService.getList(where, kUser.getuId());
		return JsonUtils.objectToJson(list);
	}
	
	@RequestMapping("getAllMonitorTrans.shtml")
	public String getAllMonitorJob(HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		return JsonUtils.objectToJson(transMonitorService.getAllMonitorTrans(kUser.getuId()));
	}
	
	@RequestMapping("getAllSuccess.shtml")
	public String getAllSuccess(HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		return JsonUtils.objectToJson(transMonitorService.getAllSuccess(kUser.getuId()));
	}
	
	@RequestMapping("getAllFail.shtml")
	public String getAllFail(HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		return JsonUtils.objectToJson(transMonitorService.getAllFail(kUser.getuId()));
	}
}
