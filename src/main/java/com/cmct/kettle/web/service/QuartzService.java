package com.cmct.kettle.web.service;

import com.cmct.kettle.core.dto.BootTablePage;
import com.cmct.kettle.core.mapper.KQuartzDao;
import com.cmct.kettle.core.model.po.KQuartz;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class QuartzService {

	
	@Autowired
	private KQuartzDao kQuartzDao;
	
	/**
	 * @Title getList
	 * @Description 获取定时策略列表
	 * @return 
	 * @throws KettleException
	 * @return List<KQuartz>
	 */
	public List<KQuartz> getList(Integer uId){
		List<KQuartz> resultList = new ArrayList<KQuartz>();
		KQuartz kQuartz = new KQuartz();
		kQuartz.setDelFlag(1);
		kQuartz.setAddUser(uId);
		resultList.addAll(kQuartzDao.select(kQuartz));
		return resultList;
	}
	
	/**
	 * @Title getList
	 * @Description 获取分页列表
	 * @param start 起始行数
	 * @param size 每页行数
	 * @param uId 用户ID
	 * @return
	 * @throws KettleException
	 * @return BootTablePage
	 */
	public BootTablePage getList(Integer start, Integer size, Integer uId){
		KQuartz kQuartz = new KQuartz();
		kQuartz.setDelFlag(1);
		kQuartz.setAddUser(uId);
		Page page = PageHelper.startPage(start,size);
		kQuartzDao.select(kQuartz);
		return new BootTablePage(page);
	}	
}