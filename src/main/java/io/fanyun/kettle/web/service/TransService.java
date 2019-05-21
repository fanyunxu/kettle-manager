package io.fanyun.kettle.web.service;

import io.fanyun.kettle.base.BaseWhere;
import io.fanyun.kettle.common.toolkit.Constant;
import io.fanyun.kettle.core.dto.BootTablePage;
import io.fanyun.kettle.core.mapper.KQuartzDao;
import io.fanyun.kettle.core.mapper.KTransDao;
import io.fanyun.kettle.core.mapper.KTransMonitorDao;
import io.fanyun.kettle.core.model.po.KQuartz;
import io.fanyun.kettle.core.model.po.KRepository;
import io.fanyun.kettle.core.model.po.KTrans;
import io.fanyun.kettle.core.model.po.KTransMonitor;
import io.fanyun.kettle.web.quartz.QuartzManager;
import io.fanyun.kettle.web.quartz.TransQuartz;
import io.fanyun.kettle.web.quartz.model.DBConnectionModel;
import io.fanyun.kettle.web.utils.CommonUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransService {

	@Autowired
	private KTransDao kTransDao;
	
	@Autowired
	private KQuartzDao kQuartzDao;
	
	@Autowired
	private io.fanyun.kettle.core.mapper.KRepositoryDao KRepositoryDao;
	
	@Autowired
	private KTransMonitorDao kTransMonitorDao;
	@Autowired
	private QuartzManager quartzManager;
	@Value("${kettle.log.file.path}")
	private String kettleLogFilePath;
	
	@Value("${kettle.file.repository}")
	private String kettleFileRepository;

	@Value("${spring.datasource.url}")
	private String jdbcDriver;

	@Value("${spring.datasource.url}")
	private String jdbcUrl;

	@Value("${spring.datasource.username}")
	private String jdbcUsername;

	@Value("${spring.datasource.password}")
	private String jdbcPassword;
	
	/**
	 * @Title getList
	 * @Description 获取列表
	 * @param uId 用户ID
	 * @return
	 * @return List<KTrans>
	 */
	public List<KTrans> getList(Integer uId){
		KTrans template = new KTrans();
		template.setAddUser(uId);
		template.setDelFlag(1);
		return kTransDao.select(template);
	}
	
	/**
	 * @Title getList
	 * @Description 获取列表
	 * @param uId 用户ID
	 * @return
	 * @return BootTablePage
	 */
	public BootTablePage getList(BaseWhere where, Integer uId){
		KTrans template = new KTrans();
		template.setAddUser(uId);
		template.setDelFlag(1);
		Page page= PageHelper.startPage(where.getPage(),where.getPageSize());
		kTransDao.getListByTrans(template);
		return  new BootTablePage(page);
	}
	
	/**
	 * @Title delete
	 * @Description 删除转换
	 * @param kTransId 转换ID
	 * @return void
	 */
	public void delete(Integer kTransId){
		KTrans kTrans = kTransDao.selectByPrimaryKey(kTransId);
		kTrans.setDelFlag(0);
		kTransDao.updateByPrimaryKeySelective(kTrans);
	}
	
	/**
	 * @Title check
	 * @Description 检查转换是否添加过
	 * @param repositoryId 资源库ID
	 * @param kTransPath 转换路径信息
	 * @param uId 用户ID
	 * @return
	 * @return boolean
	 */
	public boolean check(Integer repositoryId, String kTransPath, Integer uId){
		KTrans template = new KTrans();
		template.setDelFlag(1);
		template.setAddUser(uId);
		template.setTransRepositoryId(repositoryId);
		template.setTransPath(kTransPath);
		List<KTrans> kTransList = kTransDao.select(template);
		if (null != kTransList && kTransList.size() > 0){
			return false;
		}else{
			return true;	
		}	
	}
	
	/**
	 * @Title saveFile
	 * @Description 保存上传的转换文件
	 * @param uId 用户ID
	 * @param transFile 需要保存的转换文件
	 * @return
	 * @throws IOException
	 * @return String
	 */
	public String saveFile(Integer uId, MultipartFile transFile) throws IOException{
		return CommonUtils.saveFile(uId, kettleFileRepository, transFile);
	}
	
	/**
	 * @Title insert
	 * @Description 添加转换到数据库
	 * @param kTrans 转换对象
	 * @param uId 用户ID
	 * @param customerQuarz 用户自定义的定时策略
	 * @throws SQLException 
	 * @return void
	 */
	public void insert(KTrans kTrans, Integer uId, String customerQuarz) throws SQLException{	
		//补充添加作业信息
		//作业基础信息
		kTrans.setAddUser(uId);
		kTrans.setAddTime(new Date());
		kTrans.setEditUser(uId);
		kTrans.setEditTime(new Date());
		//作业是否被删除
		kTrans.setDelFlag(1);
		//作业是否启动
		kTrans.setTransStatus(2);
		if (StringUtils.isNotBlank(customerQuarz)){
			//添加任务执行的调度策略
			KQuartz kQuartz = new KQuartz();
			kQuartz.setAddUser(uId);
			kQuartz.setAddTime(new Date());
			kQuartz.setEditUser(uId);
			kQuartz.setEditTime(new Date());
			kQuartz.setDelFlag(1);
			kQuartz.setQuartzCron(customerQuarz);
			kQuartz.setQuartzDescription(kTrans.getTransName() + "的定时策略");
			kQuartzDao.insertSelective(kQuartz);
			//插入调度策略
			kTrans.setTransQuartz(kQuartz.getQuartzId());
		}else if (StringUtils.isBlank(customerQuarz) && new Integer(0).equals(kTrans.getTransQuartz())){
			kTrans.setTransQuartz(1);	
		}else if (StringUtils.isBlank(customerQuarz) && kTrans.getTransQuartz() == null){
			kTrans.setTransQuartz(1);
		}
		kTransDao.insert(kTrans);
	}
	
	/**
	 * @Title getTrans
	 * @Description 获取转换对象
	 * @param transId 转换ID
	 * @return
	 * @return KTrans
	 */
	public KTrans getTrans(Integer transId){
		return kTransDao.selectByPrimaryKey(transId);
	}
	
	/**
	 * @Title update
	 * @Description 更新转换信息
	 * @param kTrans 转换对象
	 * @param customerQuarz 用户自定义的定时策略
	 * @param uId 用户ID
	 * @return void
	 */
	public void update(KTrans kTrans, String customerQuarz, Integer uId){
		KTrans tmpKTrans=kTransDao.selectByPrimaryKey(kTrans.getTransId());
		KQuartz kQuartz=null;
		if (StringUtils.isNotBlank(customerQuarz)){
			Integer transQuartzId = kTrans.getTransQuartz();
			if(transQuartzId==null){
				transQuartzId=tmpKTrans.getTransQuartz();
			}
			 kQuartz = kQuartzDao.selectByPrimaryKey(transQuartzId);
			// 如果更新前选择的是自定义的，这一步要更新
			if (uId.equals(kQuartz.getAddUser())){
				kQuartz.setQuartzCron(customerQuarz);
				kQuartzDao.updateByPrimaryKeySelective(kQuartz);
			}else {
				// 如果更新前选择的是默认的定时策略，这一步要新增一个定时策略
				KQuartz kQuartzTemeplate = new KQuartz();
				kQuartzTemeplate.setAddUser(uId);
				kQuartzTemeplate.setAddTime(new Date());
				kQuartzTemeplate.setEditUser(uId);
				kQuartzTemeplate.setEditTime(new Date());
				kQuartzTemeplate.setDelFlag(1);
				kQuartzTemeplate.setQuartzCron(customerQuarz);
				kQuartzTemeplate.setQuartzDescription(kTrans.getTransName() + "的定时策略");
				 kQuartzDao.insertSelective(kQuartzTemeplate);
				//插入调度策略
				kTrans.setTransQuartz(kQuartzTemeplate.getQuartzId());
			}			
		}
		kTransDao.updateByPrimaryKeySelective(kTrans);
		//停止之前的定时策略任务 启动新策略任务
		if(1==tmpKTrans.getTransStatus()){
			removeQuartzByTrans(tmpKTrans);
			start(kTrans.getTransId());
		}
	}
	
	/**
	 * @Title start
	 * @Description 启动转换
	 * @param transId 转换ID
	 * @return void
	 */
	public void start(Integer transId){		
		// 获取到转换对象
		KTrans kTrans = kTransDao.selectByPrimaryKey(transId);
		// 获取到定时策略对象
		KQuartz kQuartz = kQuartzDao.selectByPrimaryKey(kTrans.getTransQuartz());
		// 定时策略
		String quartzCron = kQuartz.getQuartzCron();
		// 用户ID
		Integer userId = kTrans.getAddUser();
		// 获取Quartz执行的基础信息
		Map<String, String> quartzBasic = getQuartzBasic(kTrans);
		// 获取Quartz的参数
		Map<String, Object> quartzParameter = getQuartzParameter(kTrans);
		// 添加监控
		addMonitor(userId, transId);
		// 添加任务
		// 判断转换执行类型
		try {
			//如果是只执行一次
			if (new Integer(1).equals(kTrans.getTransQuartz())){
				quartzManager.addOnceJob(quartzBasic.get("jobName"), quartzBasic.get("jobGroupName"),
						quartzBasic.get("triggerName"), quartzBasic.get("triggerGroupName"),
						TransQuartz.class, quartzParameter);
			}else {// 如果是按照策略执行
				//添加任务
				quartzManager.addJob(quartzBasic.get("jobName"), quartzBasic.get("jobGroupName"),
						quartzBasic.get("triggerName"), quartzBasic.get("triggerGroupName"),
						TransQuartz.class, quartzCron, quartzParameter);
			}
		}
		catch (Exception e)
		{
			kTrans.setTransStatus(2);
			kTransDao.updateByPrimaryKeySelective(kTrans);
			return;
		}
		kTrans.setTransStatus(1);
		kTransDao.updateByPrimaryKeySelective(kTrans);
	}
	
	/**
	 * @Title stop
	 * @Description 停止转换
	 * @param transId 转换ID
	 * @return void
	 */
	public void stop(Integer transId){
		// 获取到作业对象
		KTrans kTrans = kTransDao.selectByPrimaryKey(transId);
		// 用户ID
		Integer userId = kTrans.getAddUser();
		// 获取Quartz执行的基础信息
		Map<String, String> quartzBasic = getQuartzBasic(kTrans);
		// 移除任务
		if (new Integer(1).equals(kTrans.getTransQuartz())){//如果是只执行一次
			// 一次性执行任务，不允许手动停止
			
		}else {// 如果是按照策略执行						
			quartzManager.removeJob(quartzBasic.get("jobName"), quartzBasic.get("jobGroupName"),
					quartzBasic.get("triggerName"), quartzBasic.get("triggerGroupName"));
		}	
		// 移除监控
		removeMonitor(userId, transId);
		// 更新任务状态
		kTrans.setTransStatus(2);
		kTransDao.updateByPrimaryKeySelective(kTrans);
	}

	/**
	 * 根据转换移除任务
	 * @param kTrans
	 */
	private void removeQuartzByTrans(KTrans kTrans){
		// 获取Quartz执行的基础信息
		Map<String, String> quartzBasic = getQuartzBasic(kTrans);
		quartzManager.removeJob(quartzBasic.get("jobName"), quartzBasic.get("jobGroupName"),
				quartzBasic.get("triggerName"), quartzBasic.get("triggerGroupName"));
	}

	/**
	 * @Title getQuartzBasic
	 * @Description 获取任务调度的基础信息
	 * @param kTrans 转换对象
	 * @return
	 * @return Map<String, String> 任务调度的基础信息
	 */
	private Map<String, String> getQuartzBasic(KTrans kTrans){	
		Integer userId = kTrans.getAddUser();
		Integer transRepositoryId = kTrans.getTransRepositoryId();
		String transPath = kTrans.getTransPath();
		Map<String, String> quartzBasic = new HashMap<String, String>();		
		// 拼接Quartz的任务名称
		StringBuilder jobName = new StringBuilder();
		jobName.append(Constant.JOB_PREFIX).append(Constant.QUARTZ_SEPARATE)
					.append(transRepositoryId).append(Constant.QUARTZ_SEPARATE)
					.append(transPath);
		// 拼接Quartz的任务组名称
		StringBuilder jobGroupName = new StringBuilder();
		jobGroupName.append(Constant.JOB_GROUP_PREFIX).append(Constant.QUARTZ_SEPARATE)
					.append(userId).append(Constant.QUARTZ_SEPARATE)
					.append(transRepositoryId).append(Constant.QUARTZ_SEPARATE)
					.append(transPath);
		// 拼接Quartz的触发器名称
		String triggerName = StringUtils.replace(jobName.toString(), Constant.JOB_PREFIX, Constant.TRIGGER_PREFIX);
		// 拼接Quartz的触发器组名称
		String triggerGroupName = StringUtils.replace(jobGroupName.toString(), Constant.JOB_GROUP_PREFIX, Constant.TRIGGER_GROUP_PREFIX);
		quartzBasic.put("jobName", jobName.toString());
		quartzBasic.put("jobGroupName", jobGroupName.toString());
		quartzBasic.put("triggerName", triggerName);
		quartzBasic.put("triggerGroupName", triggerGroupName);
		return quartzBasic;
	}
	
	/**
	 * @Title getQuartzParameter
	 * @Description 获取任务调度的参数
	 * @param kTrans 转换对象
	 * @return
	 * @return Map<String, Object>
	 */
	private Map<String, Object> getQuartzParameter(KTrans kTrans){
		// Quartz执行时的参数
		Map<String, Object> parameter = new HashMap<String, Object>();
		// 资源库对象
		Integer transRepositoryId = kTrans.getTransRepositoryId();
		KRepository kRepository = null;
		// 这里是判断是否为资源库中的转换还是文件类型的转换的关键点
		if (transRepositoryId != null){
			kRepository = KRepositoryDao.selectByPrimaryKey(transRepositoryId);
		}
		// 资源库对象
		parameter.put(Constant.REPOSITORYOBJECT, kRepository);
		// 数据库连接对象
		parameter.put(Constant.DBCONNECTIONOBJECT, new DBConnectionModel(jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword));
		// 转换ID
		parameter.put(Constant.TRANSID, kTrans.getTransId());
		parameter.put(Constant.JOBTYPE, 2);
		String transPath = kTrans.getTransPath();
		if (transPath.contains("/")){
			int lastIndexOf = StringUtils.lastIndexOf(transPath, "/");
			String path = transPath.substring(0, lastIndexOf);
			// 转换在资源库中的路径
			parameter.put(Constant.TRANSPATH, StringUtils.isEmpty(path) ? "/" : path);
			// 转换名称
			parameter.put(Constant.TRANSNAME, transPath.substring(lastIndexOf + 1, transPath.length()));
		}			
		// 用户ID
		parameter.put(Constant.USERID, kTrans.getAddUser());
		// 转换日志等级
		parameter.put(Constant.LOGLEVEL, kTrans.getTransLogLevel());
		// 转换日志的保存位置
		parameter.put(Constant.LOGFILEPATH, kettleLogFilePath);
		return parameter;
	}
	
	/**
	 * @Title addMonitor
	 * @Description 添加监控
	 * @param userId 用户ID
	 * @param transId 转换ID
	 * @return void
	 */
	private void addMonitor(Integer userId, Integer transId){
		KTransMonitor template = new KTransMonitor();
		template.setAddUser(userId);
		template.setMonitorTrans(transId);
		KTransMonitor templateOne = kTransMonitorDao.selectOne(template);
		if (null != templateOne){
			templateOne.setMonitorStatus(1);
			StringBuilder runStatusBuilder = new StringBuilder();
			runStatusBuilder.append(templateOne.getRunStatus())
				.append(",").append(new Date().getTime()).append(Constant.RUNSTATUS_SEPARATE);
			templateOne.setRunStatus(runStatusBuilder.toString());
			kTransMonitorDao.updateByPrimaryKeySelective(templateOne);
		}else {
			KTransMonitor kTransMonitor = new KTransMonitor();
			kTransMonitor.setMonitorTrans(transId);
			kTransMonitor.setAddUser(userId);
			kTransMonitor.setMonitorSuccess(0);
			kTransMonitor.setMonitorFail(0);
			StringBuilder runStatusBuilder = new StringBuilder();
			runStatusBuilder.append(new Date().getTime()).append(Constant.RUNSTATUS_SEPARATE);
			kTransMonitor.setRunStatus(runStatusBuilder.toString());
			kTransMonitor.setMonitorStatus(1);
			kTransMonitorDao.insert(kTransMonitor);
		}
	}
	
	/**
	 * @Title removeMonitor
	 * @Description 移除监控
	 * @param userId 用户ID
	 * @param transId 转换ID
	 * @return void
	 */
	private void removeMonitor(Integer userId, Integer transId){
		KTransMonitor template = new KTransMonitor();
		template.setAddUser(userId);
		template.setMonitorTrans(transId);
		KTransMonitor templateOne = kTransMonitorDao.selectOne(template);
		templateOne.setMonitorStatus(2);
		StringBuilder runStatusBuilder = new StringBuilder();
		runStatusBuilder.append(templateOne.getRunStatus())
			.append(new Date().getTime());
		templateOne.setRunStatus(runStatusBuilder.toString());
		kTransMonitorDao.updateByPrimaryKeySelective(templateOne);
	}
}