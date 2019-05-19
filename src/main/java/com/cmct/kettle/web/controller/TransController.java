package com.cmct.kettle.web.controller;

import com.cmct.kettle.common.toolkit.Constant;
import com.cmct.kettle.core.dto.BootTablePage;
import com.cmct.kettle.core.dto.ResultDto;
import com.cmct.kettle.core.model.po.KTrans;
import com.cmct.kettle.core.model.po.KUser;
import com.cmct.kettle.web.service.TransService;
import com.cmct.kettle.web.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/trans/")
public class TransController {

	@Autowired
	private TransService transService;
	
	
	@RequestMapping("getSimpleList.shtml")
	public String getSimpleList(HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		return JsonUtils.objectToJson(transService.getList(kUser.getuId()));
	}
	
	@RequestMapping("getList.shtml")
	public String getList(Integer offset, Integer limit, HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		BootTablePage list = transService.getList(offset, limit, kUser.getuId());
		return JsonUtils.objectToJson(list);
	}
	
	@RequestMapping("uploadTrans.shtml")
	public String uploadJob(MultipartFile transFile, HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		try {
			String saveFilePath = transService.saveFile(kUser.getuId(), transFile);
			return ResultDto.success(saveFilePath);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	@RequestMapping("insert")
	public String insert(KTrans kTrans, String customerQuarz, HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		Integer uId = kUser.getuId();		
		if (transService.check(kTrans.getTransRepositoryId(), kTrans.getTransPath(), uId)){
			try {
				transService.insert(kTrans, uId, customerQuarz);
				return ResultDto.success();
			} catch (SQLException e) {
				e.printStackTrace();
				return ResultDto.fail("添加作业失败");
			}				
		}else{
			return ResultDto.fail("该作业已经添加过了");
		}
	}
	
	@RequestMapping("getTrans.shtml")
	public String getTrans(Integer transId){
		return ResultDto.success(transService.getTrans(transId));
	}
	
	@RequestMapping("update.shtml")
	public String update(KTrans kTrans, String customerQuarz, HttpServletRequest request){
		KUser kUser = (KUser) request.getSession().getAttribute(Constant.SESSION_ID);
		transService.update(kTrans, customerQuarz, kUser.getuId());
		return ResultDto.success();

	}
	
	@RequestMapping("start.shtml")
	public String start(Integer transId){
		transService.start(transId);
		return ResultDto.success();	
	}
	
	@RequestMapping("stop.shtml")
	public String stop(Integer transId){
		transService.stop(transId);		
		return ResultDto.success();	
	}
}
