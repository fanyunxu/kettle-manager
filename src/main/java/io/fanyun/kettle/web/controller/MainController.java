package io.fanyun.kettle.web.controller;

import io.fanyun.kettle.common.toolkit.Constant;
import io.fanyun.kettle.core.dto.BootTablePage;
import io.fanyun.kettle.core.dto.ResultDto;
import io.fanyun.kettle.core.model.po.KUser;
import io.fanyun.kettle.web.service.JobMonitorService;
import io.fanyun.kettle.web.service.TransMonitorService;
import io.fanyun.kettle.web.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/main/")
public class MainController {

	@Autowired
	private TransMonitorService transMonitorService;
	
	@Autowired
	private JobMonitorService jobMonitorService;
	
	/**
	 * @Title allRuning
	 * @Description 
	 * @param request
	 * @return
	 * @return String
	 */
	@RequestMapping("allRuning.shtml")
	public String allRuning(HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		Integer allMonitorTrans = transMonitorService.getAllMonitorTrans(kUser.getuId());
		Integer allMonitorJob = jobMonitorService.getAllMonitorJob(kUser.getuId());
		Integer allRuning = allMonitorTrans + allMonitorJob; 
		return JsonUtils.objectToJson(allRuning);
	}
	
	/**
	 * @Title getTransList
	 * @Description 获取转换的Top5
	 * @param request
	 * @return
	 * @return String
	 */
	@RequestMapping("getTransList.shtml")
	public String getTransList(HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		BootTablePage list = transMonitorService.getList(kUser.getuId());
		return JsonUtils.objectToJson(list);
	}
	
	/**
	 * @Title getJobList
	 * @Description 获取作业的Top5
	 * @param request
	 * @return
	 * @return String
	 */
	@RequestMapping("getJobList.shtml")
	public String getJobList(HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		BootTablePage list = jobMonitorService.getList(kUser.getuId());
		return JsonUtils.objectToJson(list);
	}
	
	/**
	 * 
	 * @Title getKettleLine
	 * @Description TODO
	 * @return
	 * @return String
	 * @throws ParseException 
	 */
	@RequestMapping("getKettleLine.shtml")
	public String getKettleLine(HttpServletRequest request){		
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<String> dateList = new ArrayList<String>();
		for (int i = -6; i <= 0; i++){
			Calendar instance = Calendar.getInstance();
			instance.add(Calendar.DATE, i);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String dateFormat = simpleDateFormat.format(instance.getTime());
			dateList.add(dateFormat);
		}
		resultMap.put("legend", dateList);
		Map<String, Object> transLine = transMonitorService.getTransLine(kUser.getuId());
		resultMap.put("trans", transLine);
		Map<String, Object> jobLine = jobMonitorService.getJobLine(kUser.getuId());
		resultMap.put("job", jobLine);
		return ResultDto.success(resultMap);
	}
}
