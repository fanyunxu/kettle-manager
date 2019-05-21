package io.fanyun.kettle.web.controller;

import io.fanyun.kettle.base.BaseWhere;
import io.fanyun.kettle.common.toolkit.Constant;
import io.fanyun.kettle.core.dto.ResultDto;
import io.fanyun.kettle.core.model.po.KQuartz;
import io.fanyun.kettle.core.model.po.KUser;
import io.fanyun.kettle.web.service.QuartzService;
import io.fanyun.kettle.web.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/quartz/")
public class QuartzController {

	@Autowired
	private QuartzService quartzService;
	
	@RequestMapping("getSimpleList.shtml")
	public String simpleList(HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		return JsonUtils.objectToJson(quartzService.getList(kUser.getuId()));
	}
	@RequestMapping("getListPage.shtml")
	public String getListPage(BaseWhere where,HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		return JsonUtils.objectToJson(quartzService.getListPage(where,kUser.getuId()));
	}
	@RequestMapping("save.shtml")
	public String add(KQuartz quartz,HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		quartzService.save(quartz,kUser.getuId());
		return ResultDto.success();
	}
	@RequestMapping("getQuartz.shtml")
	public String getQuartz(Integer quartzId){
		return  ResultDto.success(quartzService.getQuartz(quartzId));
	}
	@GetMapping("delete.shtml")
	public String delete(Integer quartzId,HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		quartzService.delete(quartzId,kUser);
		return ResultDto.success();
	}
	@GetMapping("container/allQuartz.shtml")
	public String getContainerAllQuartz(){
		return JsonUtils.objectToJson(quartzService.getQuartzContainerList());
	}
}
