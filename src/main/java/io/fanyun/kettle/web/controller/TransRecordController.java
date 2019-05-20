package io.fanyun.kettle.web.controller;

import io.fanyun.kettle.base.BaseWhere;
import io.fanyun.kettle.common.toolkit.Constant;
import io.fanyun.kettle.core.dto.BootTablePage;
import io.fanyun.kettle.core.dto.ResultDto;
import io.fanyun.kettle.core.model.po.KUser;
import io.fanyun.kettle.web.service.TransRecordService;
import io.fanyun.kettle.web.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/trans/record/")
public class TransRecordController {

	@Autowired
	private TransRecordService transRecordService;
	
	@RequestMapping("getList.shtml")
	public String getList(BaseWhere where, Integer transId, HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		BootTablePage list = transRecordService.getList(where, kUser.getuId(), transId);
		return JsonUtils.objectToJson(list);
	}
	
	@RequestMapping("getLogContent.shtml")
	public String getLogContent(Integer recordId){
		try {
			String logContent = transRecordService.getLogContent(recordId);
			return ResultDto.success(logContent.replace("\r\n", "<br/>"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}
}
