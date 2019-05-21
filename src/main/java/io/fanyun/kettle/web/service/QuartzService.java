package io.fanyun.kettle.web.service;

import io.fanyun.kettle.base.BaseWhere;
import io.fanyun.kettle.core.dto.BootTablePage;
import io.fanyun.kettle.core.mapper.KQuartzDao;
import io.fanyun.kettle.core.model.po.KQuartz;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.fanyun.kettle.core.model.po.KUser;
import io.fanyun.kettle.core.model.vo.QuartzVo;
import io.fanyun.kettle.web.quartz.QuartzManager;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class QuartzService {

	
	@Autowired
	private KQuartzDao kQuartzDao;
	@Autowired
	private QuartzManager quartzManager;
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
		//kQuartz.setAddUser(uId);
		resultList.addAll(kQuartzDao.select(kQuartz));
		return resultList;
	}
	
	/**
	 * @Title getList
	 * @Description 获取分页列表
	 * @param uId 用户ID
	 * @return
	 * @throws KettleException
	 * @return BootTablePage
	 */
	public BootTablePage getListPage(BaseWhere where, Integer uId){
		KQuartz kQuartz = new KQuartz();
		kQuartz.setDelFlag(1);
		//kQuartz.setAddUser(uId);
		Page page = PageHelper.startPage(where.getPage(),where.getPageSize());
		kQuartzDao.select(kQuartz);
		return new BootTablePage(page);
	}

	/**
	 * 添加执行策略
	 * @param kQuartz
	 * @param getuId
	 */
	public void save(KQuartz kQuartz, Integer getuId) {
		if(kQuartz==null||kQuartz.getQuartzCron()==null||kQuartz.getQuartzDescription()==null){
			throw new RuntimeException("请完成必填项");
		}
		if(kQuartz.getQuartzId()==null){
			kQuartz.setDelFlag(1);
			kQuartz.setAddTime(new Date());
			kQuartz.setEditTime(new Date());
			kQuartz.setAddUser(getuId);
			kQuartz.setEditUser(getuId);
			kQuartzDao.insert(kQuartz);
		}else{
			kQuartz.setEditTime(new Date());
			kQuartz.setEditUser(getuId);
			kQuartzDao.updateByPrimaryKeySelective(kQuartz);
		}

	}

	public Object getQuartz(Integer quartzId) {
		return kQuartzDao.selectByPrimaryKey(quartzId);
	}

	/**
	 * 删除定时
	 * @param quartzId
	 * @param kUser
	 */
	public void delete(Integer quartzId, KUser kUser) {
		KQuartz kQuartz=new KQuartz();
		kQuartz.setQuartzId(quartzId);
		kQuartz.setEditTime(new Date());
		kQuartz.setEditUser(kUser.getuId());
		kQuartz.setDelFlag(0);
		kQuartzDao.updateByPrimaryKeySelective(kQuartz);

	}

	/**
	 * 获取所有定时
	 * @return
	 */
	public BootTablePage getQuartzContainerList(){
		List<QuartzVo> quartzVos=quartzManager.getAllJobs();
		quartzVos=quartzVos==null?new ArrayList<>():quartzVos;
		return  new BootTablePage(quartzVos.size(),quartzVos);
	}
}