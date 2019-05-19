package com.cmct.kettle.web.service;

import com.cmct.kettle.common.toolkit.MD5Utils;
import com.cmct.kettle.core.dto.BootTablePage;
import com.cmct.kettle.core.mapper.KUserDao;
import com.cmct.kettle.core.model.po.KUser;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	private KUserDao kUserDao;
		
	/**
	 * @Title login
	 * @Description 登陆
	 * @param kUser 用户信息对象
	 * @return
	 * @return KUser
	 */
	public KUser login(KUser kUser){		
		KUser template = new KUser();
		template.setDelFlag(1);
		template.setuAccount(kUser.getuAccount());
		KUser user = kUserDao.selectOne(template);
		if (null != user){
			if (user.getuPassword().equals(MD5Utils.Encrypt(kUser.getuPassword(), true))){
				return user;
			}
			return null;
		}
		return null;
	}
	
	/**
	 * @Title isAdmin
	 * @Description 用户是否为管理员
	 * @param uId 用户ID
	 * @return
	 * @return boolean
	 */
	public boolean isAdmin(Integer uId){
		KUser kUser = kUserDao.selectByPrimaryKey(uId);
		if ("admin".equals(kUser.getuAccount())){
			return true;
		}else {
			return false;	
		}
	}
	
	/**
	 * @Title getList
	 * @Description 获取用户分页列表
	 * @param start 其实行数
	 * @param size 每页显示行数
	 * @return 
	 * @return BootTablePage
	 */
	public BootTablePage getList(Integer start, Integer size){
		KUser template = new KUser();
		template.setDelFlag(1);
		Page page= PageHelper.startPage(start,size);
		kUserDao.select(template);
		return new BootTablePage(page);
	}
	
	/**
	 * @Title delete
	 * @Description 删除用户
	 * @param uId 用户ID
	 * @return void
	 */
	public void delete(Integer uId){
		KUser kUser = kUserDao.selectByPrimaryKey(uId);
		kUser.setDelFlag(0);
		kUserDao.updateByPrimaryKeySelective(kUser);
	}
	
	/**
	 * @Title insert
	 * @Description 插入一个用户
	 * @param kUser
	 * @return void
	 */
	public void insert(KUser kUser){	
	}	
}