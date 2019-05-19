package com.cmct.kettle.web.service;

import com.cmct.kettle.common.toolkit.Constant;
import com.cmct.kettle.core.dto.BootTablePage;
import com.cmct.kettle.core.mapper.KJobDao;
import com.cmct.kettle.core.mapper.KJobMonitorDao;
import com.cmct.kettle.core.mapper.KQuartzDao;
import com.cmct.kettle.core.model.po.KJob;
import com.cmct.kettle.core.model.po.KJobMonitor;
import com.cmct.kettle.core.model.po.KQuartz;
import com.cmct.kettle.core.model.po.KRepository;
import com.cmct.kettle.web.quartz.JobQuartz;
import com.cmct.kettle.web.quartz.QuartzManager;
import com.cmct.kettle.web.quartz.model.DBConnectionModel;
import com.cmct.kettle.web.utils.CommonUtils;
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
public class JobService {

	
	@Autowired
	private KJobDao kJobDao;
	
	@Autowired
	private KQuartzDao kQuartzDao;
	
	@Autowired
	private com.cmct.kettle.core.mapper.KRepositoryDao kRepositoryDao;
	
	@Autowired
	private KJobMonitorDao kJobMonitorDao;
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
	 * @param start 其实行数
	 * @param size 获取数据的条数
	 * @param uId 用户ID
	 * @return
	 * @return BootTablePage
	 */
	public BootTablePage getList(Integer start, Integer size, Integer uId){
		KJob template = new KJob();
		template.setAddUser(uId);
		template.setDelFlag(1);
		Page<KJob> page =PageHelper.startPage(start,size);
  		kJobDao.select(template);
		BootTablePage bootTablePage = new BootTablePage();
		bootTablePage.setRows(page.getResult());
		bootTablePage.setTotal(page.getTotal());
		return bootTablePage;
	}
	
	/**
	 * @Title getList
	 * @Description 获取列表
	 * @param uId 用户ID
	 * @return
	 * @return List<KJob>
	 */
	public List<KJob> getList(Integer uId){
		KJob template = new KJob();
		template.setAddUser(uId);
		template.setDelFlag(1);
		return kJobDao.select(template);
	}
	
	/**
	 * @Title delete
	 * @Description 删除作业
	 * @param jobId 作业ID
	 * @return void
	 */
	public void delete(Integer jobId){
		KJob kJob = kJobDao.selectByPrimaryKey(jobId);
		kJob.setDelFlag(0);
		kJobDao.updateByPrimaryKeySelective(kJob);
	}
	
	/**
	 * @Title check
	 * @Description 检查当前作业是否可以插入到数据库
	 * @param repositoryId 资源库ID
	 * @param jobPath 作业路径
	 * @param uId 用户ID
	 * @return
	 * @return boolean
	 */
	public boolean check(Integer repositoryId, String jobPath, Integer uId){
		KJob template = new KJob();
		template.setDelFlag(1);
		template.setAddUser(uId);
		template.setJobRepositoryId(repositoryId);
		template.setJobPath(jobPath);
		List<KJob> kJobList = kJobDao.select(template);
		if (null != kJobList && kJobList.size() > 0){
			return false;
		}else{
			return true;	
		}	
	}
		
	/**
	 * @Title saveFile
	 * @Description 保存上传的作业文件
	 * @param uId 用户ID
	 * @param jobFile 上传的作业文件
	 * @return
	 * @throws IOException
	 * @return String
	 */
	public String saveFile(Integer uId, MultipartFile jobFile) throws IOException{
		return CommonUtils.saveFile(uId, kettleFileRepository, jobFile);
	}
	
	/**
	 * @Title insert
	 * @Description 插入作业到数据库
	 * @param kJob 作业信息
	 * @param uId 用户ID
	 * @param customerQuarz 自定义定时策略
	 * @throws SQLException 
	 * @return void
	 */
	public void insert(KJob kJob, Integer uId, String customerQuarz) throws SQLException{
		//补充添加作业信息
		//作业基础信息
		kJob.setAddUser(uId);
		kJob.setAddTime(new Date());
		kJob.setEditUser(uId);
		kJob.setEditTime(new Date());
		//作业是否被删除
		kJob.setDelFlag(1);
		//作业是否启动
		kJob.setJobStatus(2);
		if (StringUtils.isNotBlank(customerQuarz)){
			//添加任务执行的调度策略
			KQuartz kQuartz = new KQuartz();
			kQuartz.setAddUser(uId);
			kQuartz.setAddTime(new Date());
			kQuartz.setEditUser(uId);
			kQuartz.setEditTime(new Date());
			kQuartz.setDelFlag(1);
			kQuartz.setQuartzCron(customerQuarz);
			kQuartz.setQuartzDescription(kJob.getJobName() + "的定时策略");
			kQuartzDao.insertSelective(kQuartz);
			//插入调度策略
			kJob.setJobQuartz(kQuartz.getQuartzId());
		}else if (StringUtils.isBlank(customerQuarz) && new Integer(0).equals(kJob.getJobQuartz())){
			kJob.setJobQuartz(1);	
		}else if (StringUtils.isBlank(customerQuarz) && kJob.getJobQuartz() == null){
			kJob.setJobQuartz(1);
		}
		kJobDao.insert(kJob);
	}
	
	/**
	 * @Title getJob
	 * @Description 获取作业信息
	 * @param jobId 作业ID
	 * @return
	 * @return KJob
	 */
	public KJob getJob(Integer jobId){

		return kJobDao.selectByPrimaryKey(jobId);
	}
	
	/**
	 * @Title update
	 * @Description 更新作业信息
	 * @param kJob 作业对象
	 * @param customerQuarz 自定义定时策略
	 * @param uId 用户ID
	 * @return void
	 */
	public void update(KJob kJob, String customerQuarz, Integer uId){
		if (StringUtils.isNotBlank(customerQuarz)){
			Integer jobQuartzId = kJob.getJobQuartz();
			KQuartz kQuartz = kQuartzDao.selectByPrimaryKey(jobQuartzId);
			if (uId.equals(kQuartz.getAddUser())){// 如果更新前选择的是自定义的，这一步要更新
				kQuartz.setQuartzCron(customerQuarz);
				kQuartzDao.updateByPrimaryKeySelective(kQuartz);
			}else {// 如果更新前选择的是默认的定时策略，这一步要新增一个定时策略
				KQuartz kQuartzTemeplate = new KQuartz();
				kQuartzTemeplate.setAddUser(uId);
				kQuartzTemeplate.setAddTime(new Date());
				kQuartzTemeplate.setEditUser(uId);
				kQuartzTemeplate.setEditTime(new Date());
				kQuartzTemeplate.setDelFlag(1);
				kQuartzTemeplate.setQuartzCron(customerQuarz);
				kQuartzTemeplate.setQuartzDescription(kJob.getJobName() + "的定时策略");
				 kQuartzDao.insertSelective(kQuartzTemeplate);
				//插入调度策略
				kJob.setJobQuartz(kQuartzTemeplate.getQuartzId());
			}			
		}
		kJobDao.updateByPrimaryKeySelective(kJob);
	}
	
	
	/**
	 * @Title start
	 * @Description 启动作业
	 * @param jobId 作业ID
	 * @return void
	 */
	public void start(Integer jobId){
		// 获取到作业对象
		KJob kJob = kJobDao.selectByPrimaryKey(jobId);
		// 获取到定时策略对象		
		KQuartz kQuartz = kQuartzDao.selectByPrimaryKey(kJob.getJobQuartz());
		// 定时策略
		String quartzCron = kQuartz.getQuartzCron();
		// 用户ID
		Integer userId = kJob.getAddUser();
		// 获取调度任务的基础信息
		Map<String, String> quartzBasic = getQuartzBasic(kJob);
		// Quartz执行时的参数
		Map<String, Object> quartzParameter = getQuartzParameter(kJob);
		// 添加监控
		addMonitor(userId, jobId);
		try {
			// 判断作业执行类型
			//如果是只执行一次
			if (new Integer(1).equals(kJob.getJobQuartz())){
				quartzManager.addOnceJob(quartzBasic.get("jobName"), quartzBasic.get("jobGroupName"),
						quartzBasic.get("triggerName"), quartzBasic.get("triggerGroupName"), JobQuartz.class, quartzParameter);
			}else {// 如果是按照策略执行
				//添加任务
				quartzManager.addJob(quartzBasic.get("jobName"), quartzBasic.get("jobGroupName"),
						quartzBasic.get("triggerName"), quartzBasic.get("triggerGroupName"),
						JobQuartz.class, quartzCron, quartzParameter);
			}
		}
		catch (Exception e) {
			kJob.setJobStatus(2);
			kJobDao.updateByPrimaryKeySelective(kJob);
			return;
		}
		kJob.setJobStatus(1);
		kJobDao.updateByPrimaryKeySelective(kJob);
	}
	
	/**
	 * @Title stop
	 * @Description 停止作业
	 * @param jobId 作业ID
	 * @return void
	 */
	public void stop(Integer jobId){		
		// 获取到作业对象
		KJob kJob = kJobDao.selectByPrimaryKey(jobId);
		// 用户ID
		Integer userId = kJob.getAddUser();
		// 获取调度任务的基础信息
		Map<String, String> quartzBasic = getQuartzBasic(kJob);
		//如果是只执行一次
		if (new Integer(1).equals(kJob.getJobQuartz())){
			// 一次性执行任务，不允许手动停止
		}else {// 如果是按照策略执行
			//移除任务
			quartzManager.removeJob(quartzBasic.get("jobName"), quartzBasic.get("jobGroupName"),
					quartzBasic.get("triggerName"), quartzBasic.get("triggerGroupName"));
		}	
		// 移除监控
		removeMonitor(userId, jobId);
		kJob.setJobStatus(2);
		kJobDao.updateByPrimaryKeySelective(kJob);
	}
	
	/**
	 * @Title getQuartzBasic
	 * @Description 获取任务调度的基础信息
	 * @return
	 * @return Map<String, String> 任务调度的基础信息
	 */
	private Map<String, String> getQuartzBasic(KJob kJob){	
		Integer userId = kJob.getAddUser();
		Integer transRepositoryId = kJob.getJobRepositoryId();
		String jobPath = kJob.getJobPath();
		Map<String, String> quartzBasic = new HashMap<String, String>();		
		// 拼接Quartz的任务名称
		StringBuilder jobName = new StringBuilder();
		jobName.append(Constant.JOB_PREFIX).append(Constant.QUARTZ_SEPARATE)
					.append(transRepositoryId).append(Constant.QUARTZ_SEPARATE)
					.append(jobPath);
		// 拼接Quartz的任务组名称
		StringBuilder jobGroupName = new StringBuilder();
		jobGroupName.append(Constant.JOB_GROUP_PREFIX).append(Constant.QUARTZ_SEPARATE)
					.append(userId).append(Constant.QUARTZ_SEPARATE)
					.append(transRepositoryId).append(Constant.QUARTZ_SEPARATE)
					.append(jobPath);
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
	 * @return
	 * @return Map<String, Object>
	 */
	private Map<String, Object> getQuartzParameter(KJob kJob){
		// Quartz执行时的参数
		Map<String, Object> parameter = new HashMap<String, Object>();
		// 资源库对象
		Integer transRepositoryId = kJob.getJobRepositoryId();
		KRepository kRepository = null;
		// 这里是判断是否为资源库中的转换还是文件类型的转换的关键点
		if (transRepositoryId != null){
			kRepository = kRepositoryDao.selectByPrimaryKey(transRepositoryId);
		}
		// 资源库对象
		parameter.put(Constant.REPOSITORYOBJECT, kRepository);
		// 数据库连接对象
		parameter.put(Constant.DBCONNECTIONOBJECT, new DBConnectionModel(jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword));
		// 转换ID
		parameter.put(Constant.TRANSID, kJob.getJobId());
		parameter.put(Constant.JOBTYPE, 1);
		String jobPath = kJob.getJobPath();
		if (jobPath.contains("/")){
			int lastIndexOf = StringUtils.lastIndexOf(jobPath, "/");
			String path = jobPath.substring(0, lastIndexOf);
			// 转换在资源库中的路径
			parameter.put(Constant.TRANSPATH, StringUtils.isEmpty(path) ? "/" : path);
			// 转换名称
			parameter.put(Constant.TRANSNAME, jobPath.substring(lastIndexOf + 1, jobPath.length()));
		}			
		// 用户ID
		parameter.put(Constant.USERID, kJob.getAddUser());
		// 转换日志等级
		parameter.put(Constant.LOGLEVEL, kJob.getJobLogLevel());
		// 转换日志的保存位置
		parameter.put(Constant.LOGFILEPATH, kettleLogFilePath);
		return parameter;
	}
	
	
	
	/**
	 * @Title addMonitor
	 * @Description 添加监控
	 * @param userId 用户ID
	 * @return void
	 */
	private void addMonitor(Integer userId, Integer jobId){
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(userId);
		template.setMonitorJob(jobId);
		KJobMonitor templateOne = kJobMonitorDao.selectOne(template);
		if (null != templateOne){
			templateOne.setMonitorStatus(1);
			StringBuilder runStatusBuilder = new StringBuilder();
			runStatusBuilder.append(templateOne.getRunStatus())
				.append(",").append(new Date().getTime()).append(Constant.RUNSTATUS_SEPARATE);
			templateOne.setRunStatus(runStatusBuilder.toString());
			kJobMonitorDao.updateByPrimaryKeySelective(templateOne);
		}else {
			KJobMonitor kJobMonitor = new KJobMonitor();
			kJobMonitor.setMonitorJob(jobId);
			kJobMonitor.setAddUser(userId);
			kJobMonitor.setMonitorSuccess(0);
			kJobMonitor.setMonitorFail(0);
			StringBuilder runStatusBuilder = new StringBuilder();
			runStatusBuilder.append(new Date().getTime()).append(Constant.RUNSTATUS_SEPARATE);
			kJobMonitor.setRunStatus(runStatusBuilder.toString());
			kJobMonitor.setMonitorStatus(1);
			kJobMonitorDao.insert(kJobMonitor);
		}
	}
	
	/**
	 * @Title removeMonitor
	 * @Description 移除监控
	 * @param userId 用户ID
	 * @return void
	 */
	private void removeMonitor(Integer userId, Integer jobId){
		KJobMonitor template = new KJobMonitor();
		template.setAddUser(userId);
		template.setMonitorJob(jobId);
		KJobMonitor templateOne = kJobMonitorDao.selectOne(template);
		templateOne.setMonitorStatus(2);
		StringBuilder runStatusBuilder = new StringBuilder();
		runStatusBuilder.append(templateOne.getRunStatus())
			.append(new Date().getTime());
		templateOne.setRunStatus(runStatusBuilder.toString());
		kJobMonitorDao.updateByPrimaryKeySelective(templateOne);
	}
	
}
