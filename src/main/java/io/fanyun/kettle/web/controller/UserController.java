package io.fanyun.kettle.web.controller;

import io.fanyun.kettle.base.BaseWhere;
import io.fanyun.kettle.core.dto.ResultDto;
import io.fanyun.kettle.web.service.UserService;
import io.fanyun.kettle.web.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/")
public class UserController {

	@Autowired
	private UserService userService;
	
	@RequestMapping("getList.shtml")
	public String getList(BaseWhere where){
		return JsonUtils.objectToJson(userService.getList(where));
	}
	
	@RequestMapping("delete.shtml")
	public String delete(Integer uId){
		userService.delete(uId);
		return ResultDto.success();
	}
	
	@RequestMapping("resetPassword.shtml")
	public String resetPassword(){
		
		return ResultDto.success();
	}
}
